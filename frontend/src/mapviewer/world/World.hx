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
package mapviewer.world;

import mapviewer.block.Block;
import mapviewer.block.Blocks;
import mapviewer.js.Utils;
import haxe.Timer;
import mapviewer.logging.Logger;
import mapviewer.renderer.webgl.WebGLChunk.WebGLSnapshot;
import mapviewer.worker.WorkerWorldProxy;
import mapviewer.world.World.BuildJob;
class World {

    private static var logger : Logger = new Logger("World");

    public var chunks : Map<String, Chunk>;

    public var currentTime : Int = 6000;
    private var proxy : WorkerWorldProxy;

    public function new(?haveProxy : Bool = true) {
        chunks = new Map<String, Chunk>();
        if (haveProxy) proxy = new WorkerWorldProxy(this);
        new Timer(Std.int(1000/20)).run = tick;
    }

    private function tick() {
        currentTime = (currentTime + 1) % 24000;
		for (build in toBuild) {
			proxy.build(build.chunk, build.i);
		}
		toBuild = new Map();
    }

    public function writeRequestChunk(x : Int, z : Int) {
       proxy.requestChunk(x, z);
    }

    public function addChunk(chunk : Chunk) : Bool {
        var key = chunkKey(chunk.x, chunk.z);
        if (chunks[key] != null) {
            // Chunk is already loaded ignore it
            return false;
        }
        chunks[key] = chunk;
        for (x in -1 ... 2) {
            for (z in -1 ... 2) {
                var c = getChunk(chunk.x + x, chunk.z + z);
                if (c != null) c.rebuild();
            }
        }
        chunk.rebuild();
		return true;
    }

    public function removeChunk(x : Int, z : Int) {
        var chunk = getChunk(x, z);
		if (chunk == null) return;
        chunks.remove(chunkKey(x, z));
        chunk.unload(Main.renderer);
		if (proxy != null) proxy.removeChunk(x, z);
    }

    // Build related methods
	private static var toBuild : Map<String, {chunk : Chunk, i : Int}> = new Map();

    public function requestBuild(chunk : Dynamic, i : Int) {
		toBuild[buildKey(chunk.x, chunk.z, i)] = {chunk: chunk, i: i};
    }
	
	public static function buildKey(x : Int, z : Int, i : Int) : String {
        return '$x:$z@$i';
    }
	
    public function newChunk() : Chunk { throw "NYI"; return null; }
    public function queueCompare(a : BuildJob, b : BuildJob) : Int { throw "NYI"; return 0; }

    // General methods

    private var cacheX : Int;
    private var cacheZ : Int;
    private var cacheChunk : Chunk;

    public function getChunk(x : Int, z : Int) : Chunk {
        if (cacheChunk != null && cacheX == x && cacheZ == z) {
            return cacheChunk;
        }
        cacheX = x;
        cacheZ = z;
        cacheChunk = chunks[chunkKey(x, z)];
        return cacheChunk;
    }

    public function getBlock(x : Int, y : Int, z : Int) : Block {
        if (y < 0 || y > 255) return Blocks.AIR;
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return Blocks.NULL_BLOCK;
        return chunk.getBlock(x & 0xF, y, z & 0xF);
    }

    public function getLight(x : Int, y : Int, z : Int) : Int {
        if (y < 0 || y > 255) return 0;
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return 0;
        return chunk.getLight(x & 0xF, y, z & 0xF);
    }

    public function getSky(x : Int, y : Int, z : Int) : Int {
        if (y < 0 || y > 255) return 15;
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return 15;
        return chunk.getSky(x & 0xF, y, z & 0xF);
    }

    public function isLoaded(x : Int, y : Int, z : Int) : Bool {
        if (y < 0 || y > 255) return false;
        return getChunk(x >> 4, z >> 4) != null;
    }

    public static function chunkKey(x : Int, z : Int) : String {
        return '$x:$z';
    }
}

class BuildJob {
	
	public var chunk : Chunk;
	public var i : Int;
	
	public function new(chunk : Chunk, i : Int)  {
		this.chunk = chunk;
		this.i = i;
	}
	
	public function exec(snapshot : Dynamic, endTime : Int) : Dynamic {
		return chunk.buildSection(i, snapshot, endTime);
	}
	
	public function drop(sn : Dynamic) {
		var snapshot : WebGLSnapshot = cast sn;
		snapshot.builder.free();
		snapshot.builderTrans.free();
	}
}