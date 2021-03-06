package joe2k01.superdevelopers.keyswitch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends Activity {
    Button reboot;
    int out;
    java.lang.Process p;
    DataOutputStream run;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reboot = (Button) findViewById(R.id.btn_reboot);
        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader("/system/build.prop"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        if (line.matches("qemu.hw.mainkeys=0")) {
                            out = 1;
                            // if a line of the build.prop file matches the string out is 1
                        } else {
                            out = 0;
                            // else out is 0
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                        if (out == 1) {
                            Log.d("key", "qemu");
                            p = Runtime.getRuntime().exec("su");
                            // Get root
                            run = new DataOutputStream(p.getOutputStream());
                            run.writeBytes("mount -o rw,remount,rw /system\n");
                            run.flush();
                            // Mount system as read-write
                            run.writeBytes("sed -i '$d' /system/build.prop\n");
                            run.flush();
                            run.writeBytes("sed -i '124s/#key/key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            run.writeBytes("sed -i '161s/#key/key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            run.writeBytes("sed -i '180s/#key/key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            run.writeBytes("sed -i '194s/#key/key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            // Change strings inside Generic.kl and build.prop
                            run.writeBytes("reboot\n");
                        }
                        if (out == 0) {
                            Log.d("key", "nothing");
                            // Get root
                            p = Runtime.getRuntime().exec("su");
                            run = new DataOutputStream(p.getOutputStream());
                            run.writeBytes("mount -o rw,remount,rw /system\n");
                            run.flush();
                            // Mount system as read-write
                            run.writeBytes("echo 'qemu.hw.mainkeys=0' >> /system/build.prop\n");
                            run.flush();
                            run.writeBytes("sed -i '124s/key/#key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            run.writeBytes("sed -i '161s/key/#key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            run.writeBytes("sed -i '180s/key/#key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            run.writeBytes("sed -i '194s/key/#key/g' /system/usr/keylayout/Generic.kl\n");
                            run.flush();
                            // Change strings inside Generic.kl and build.prop
                            run.writeBytes("reboot\n");
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


    }
}
