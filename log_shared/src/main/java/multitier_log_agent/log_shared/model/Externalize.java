package multitier_log_agent.log_shared.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Externalize {
    
    private Externalize() {
        
    }
    
    public static void fromByteArray(byte[] objectBytes, Externalizable target)
            throws ClassNotFoundException, IOException {
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(objectBytes));

            target.readExternal(ois);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static byte[] toByteArray(Externalizable source) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        byte[] res = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            source.writeExternal(oos);
            oos.flush();

            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }
}
