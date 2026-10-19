/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author andras
 */
public class ExtDataInputStream extends DataInputStream {

    public ExtDataInputStream(InputStream in) {
        super(in);
    }

    public final long skipBytesL(long n) throws IOException {
        long total = 0;
        long cur = 0;

        while ((total < n) && ((cur = in.skip(n - total)) > 0)) {
            total += cur;
        }

        return total;
    }

}
