package android.content.res;
import java.io.*;
public class AssetManager { private final File root; public AssetManager(File r){root=r;}
 public String[] list(String p){ File f=p.isEmpty()?root:new File(root,p); String[] l=f.list(); return l==null?new String[0]:l; }
 public InputStream open(String p) throws IOException { return new FileInputStream(new File(root,p)); } }
