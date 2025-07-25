package mindustry.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;

import static mindustry.Vars.*;

public class CacheLayer{
    public static CacheLayer

    water, mud, cryofluid, tar, slag, arkycite,
    space, normal, walls;

    public static CacheLayer[] all = {};

    public int id;
    public boolean liquid;

    /** Registers cache layers that will render before the 'normal' layer. */
    public static void add(CacheLayer... layers){
        for(var layer : layers){
            //7 = 'normal' index
            add(7, layer);
        }
    }

    /** Register CacheLayers at the end of the array. This will render over "normal" tiles. This is likely not the method you want to use. */
    public static void addLast(CacheLayer... layers){
        int newSize = all.length + layers.length;
        var prev = all;
        //reallocate the array and copy everything over; performance matters very little here anyway
        all = new CacheLayer[newSize];
        System.arraycopy(prev, 0, all, 0, prev.length);
        System.arraycopy(layers, 0, all, prev.length, layers.length);

        for(int i = 0; i < all.length; i++){
            all[i].id = i;
        }
    }

    /** Adds a cache layer at a certain position. All layers >= this index are shifted upwards.*/
    public static void add(int index, CacheLayer layer){
        index = Mathf.clamp(index, 0, all.length - 1);

        var prev = all;
        all = new CacheLayer[all.length + 1];

        System.arraycopy(prev, 0, all, 0, index);
        System.arraycopy(prev, index, all, index + 1, prev.length - index);

        all[index] = layer;

        for(int i = 0; i < all.length; i++){
            all[i].id = i;
        }
    }

    /** Loads default cache layers. */
    public static void init(){
        addLast(
            water = new ShaderLayer(Shaders.water),
            mud = new ShaderLayer(Shaders.mud),
            tar = new ShaderLayer(Shaders.tar),
            slag = new ShaderLayer(Shaders.slag),
            arkycite = new ShaderLayer(Shaders.arkycite),
            cryofluid = new ShaderLayer(Shaders.cryofluid),
            space = new ShaderLayer(Shaders.space, false),
            normal = new CacheLayer(),
            walls = new CacheLayer()
        );
    }

    /** Called before the cache layer begins rendering. Begin FBOs here. */
    public void begin(){

    }

    /** Called after the cache layer ends rendering. Blit FBOs here. */
    public void end(){

    }

    public static class ShaderLayer extends CacheLayer{
        public @Nullable Shader shader;

        public ShaderLayer(Shader shader){
            this(shader, true);
        }

        public ShaderLayer(@Nullable Shader shader, boolean liquid){
            this.liquid = liquid;
            this.shader = shader;
        }

        @Override
        public void begin(){
            if(!renderer.animateWater) return;

            renderer.effectBuffer.begin();
            Core.graphics.clear(Color.clear);
            renderer.blocks.floor.beginDraw();
        }

        @Override
        public void end(){
            if(!renderer.animateWater) return;

            renderer.effectBuffer.end();
            renderer.effectBuffer.blit(shader);
            renderer.blocks.floor.beginDraw();
        }
    }
}
