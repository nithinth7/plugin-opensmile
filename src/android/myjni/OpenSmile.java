package myjni;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.Environment;
import com.audeering.opensmile.androidtemplate.Config;
import com.audeering.opensmile.androidtemplate.OpenSmilePlugins;
import com.audeering.opensmile.androidtemplate.SmileJNI;
import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.io.PrintWriter;
import android.util.Base64;

public class OpenSmile extends CordovaPlugin {
    OpenSmilePlugins m;
    Config conf;
	String fpath = "", fpath1 = "", pname = "";
    
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) 
        throws JSONException {
        if (action.equals("start")) {
            String name = data.getString(0);
            String config = data.getString(1);
			//path = path.substring(7,path.length());
            
            setupAssets();
            try {
                //m = new OpenSmilePlugins(cordova.getActivity(), name);
				pname = cordova.getActivity().getPackageName();
				fpath1 = "/Android/data/" + pname + "/files";
				//fpath1 =  filePath;
				fpath = Environment.getExternalStorageDirectory().getAbsolutePath() + fpath1;
				//fpath = m.getFilePath();
                callSmileExtract(config, fpath);
				
            } catch (Exception e) {
                System.out.println("Exception" + e);
            }
			
			String message = "Filepath:" + fpath + "/" + name;
            callbackContext.success(message);
            return true;
        } else if (action.equals("stop")) {
                String name = data.getString(0);
                String message = "SmileExtract: " + name;
                callbackContext.success(message);
            try {
                SmileJNI.SMILEndJNI();
                m.closeFile();
            } catch (Exception e) {
                System.out.println("Exception" + e);
            }
            return true;
        } else {
             return false;
        }
    }

    void callSmileExtract(String config, String fpath) {
        final SmileThread obj = new SmileThread(config, fpath);
        final Thread newThread = new Thread(obj);
        newThread.start();
    }

    class SmileThread implements Runnable {
        String conf = "";
		String dataPath = "";
		String filePath = "";
        SmileThread(String config, String fpath) {
			conf = cordova.getActivity().getCacheDir() + "/" + config;
			filePath = fpath;
			dataPath = fpath + "/test50.bin";
		}
		@Override
        public void run() {
            SmileJNI.SMILExtractJNI(conf, 1, dataPath);
			try {
                byte[] b = IOUtil.readFile(dataPath);
                final String b64 = Base64.encodeToString(b,Base64.DEFAULT);
                String dataPathText = filePath + "/test50.txt";
                try {
                    File f = new File(dataPathText);
                    FileOutputStream fos = new FileOutputStream(f);
                    PrintWriter pw = new PrintWriter(fos);
                    pw.print(b64);
                    pw.flush();
                    pw.close();
                    fos.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setupAssets() {
        ArrayList<OpenSmilePlugins> ans = new ArrayList<>();
        ans.add(m);
        conf = new Config(ans);
        String[] assets = conf.assets;
        //SHOULD BE MOVED TO: /data/user/0/com.audeering.opensmile.androidtemplate/cache/
        ContextWrapper cw = new ContextWrapper(this.cordova.getActivity().getApplicationContext());
        String confcach = cw.getCacheDir() + "/" ;
        AssetManager assetManager = this.cordova.getActivity().getAssets();
        for(String filename : assets) {
            try {
                InputStream in = assetManager.open(filename);
                String out = confcach + filename;
                File outFile = new File(out);
                FileOutputStream outst = new FileOutputStream(outFile);
                copyFile(in, outst);
                in.close();
                outst.flush();
                outst.close();
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
