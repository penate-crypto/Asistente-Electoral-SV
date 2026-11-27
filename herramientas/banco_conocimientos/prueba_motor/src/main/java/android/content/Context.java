package android.content;
import android.content.res.AssetManager;
public class Context { private final AssetManager a; public Context(AssetManager a){this.a=a;} public AssetManager getAssets(){return a;} public Context getApplicationContext(){return this;} }
