/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.thinkcraft.html.client.worker;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.Worker;
import uk.co.thinkofdeath.thinkcraft.html.client.MapViewer;
import uk.co.thinkofdeath.thinkcraft.html.shared.serialize.JsObjectSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.worker.Message;
import uk.co.thinkofdeath.thinkcraft.shared.worker.Messages;
import uk.co.thinkofdeath.thinkcraft.shared.worker.WorkerMessage;

import java.util.ArrayList;

public class WorkerPool {

    private final MapViewer mapViewer;

    private ArrayList<PooledWorker> workers = new ArrayList<>();

    /**
     * Creates a worker pool with a limit of the number of workers created
     *
     * @param mapViewer
     *         The map viewer that owns this pool
     * @param limit
     *         The max number of workers
     */
    public WorkerPool(MapViewer mapViewer, int limit) {
        this.mapViewer = mapViewer;
        for (int i = 0; i < limit; i++) {
            workers.add(new PooledWorker(Browser.getWindow().newWorker(
                    "./mapviewerworker/mapviewerworker.worker.js"
            ), i));
        }
    }

    /**
     * Returns whether a worker is 'free' (has less tasks left than maxTasks)
     *
     * @param maxTasks
     *         The limit on the number of tasks a worker can have before it is considered busy
     * @return Whether a worker is free for processing
     */
    public boolean hasFreeWorker(int maxTasks) {
        for (PooledWorker worker : workers) {
            if (worker.noOfTasks <= maxTasks) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends the message to a free worker. If all is set then all workers will get the message but only one will reply
     *
     * @param msg
     *         The message to send
     * @param all
     * @param transferables
     */
    public void sendMessage(WorkerMessage msg, boolean all, Object... transferables) {
        PooledWorker lowestWorker = workers.get(0);
        for (int i = 1; i < workers.size(); i++) {
            if (lowestWorker.noOfTasks > workers.get(i).noOfTasks) {
                lowestWorker = workers.get(i);
            }
        }

        JsObjectSerializer serializer = JsObjectSerializer.newInstance();
        Messages.write(msg, serializer);

        if (all) {
            for (PooledWorker worker : workers) {
                worker.noOfTasks++;
                serializer.putBoolean("return", worker == lowestWorker);
                postMessage(worker.worker, serializer, transferables);
            }
        } else {
            lowestWorker.noOfTasks++;
            serializer.putBoolean("return", true);
            postMessage(lowestWorker.worker, serializer, transferables);
        }
    }

    public void sendMessage(int target, WorkerMessage msg, boolean reply, Object... transferables) {
        PooledWorker worker = workers.get(target);
        if (reply) {
            worker.noOfTasks++;
        }
        JsObjectSerializer serializer = JsObjectSerializer.newInstance();
        Messages.write(msg, serializer);
        postMessage(worker.worker, serializer, transferables);
    }

    /**
     * Dump the worker pool's state into the console. Used for debugging
     */
    public void dump() {
        StringBuilder out = new StringBuilder("WorkerPool: ");
        for (int i = 0; i < workers.size(); i++) {
            out.append(i);
            out.append("[");
            out.append(workers.get(i).noOfTasks);
            out.append("] ");
        }
        System.out.println(out.toString());
    }

    // Support for transferables
    private native void postMessage(Worker worker, Object msg, Object[] transferables)/*-{
        worker.postMessage(msg, transferables);
    }-*/;

    private class PooledWorker implements EventListener {

        private final Worker worker;
        private int noOfTasks = 0;
        private final int id;

        public PooledWorker(Worker worker, int id) {
            this.id = id;
            this.worker = worker;
            worker.setOnmessage(this);
        }

        @Override
        public void handleEvent(Event evt) {
            noOfTasks--;
            JsObjectSerializer serializer = JsObjectSerializer.from(((MessageEvent) evt).getData());
            Message message = Messages.read(serializer);
            if (message instanceof WorkerMessage) {
                ((WorkerMessage) message).setSender(id);
            }
            message.handle(mapViewer.getMessageHandler());
        }
    }
}
