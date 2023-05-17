package org.example.base;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class Piped {
    public static void main(String[] args) throws Exception {
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        /* 将输出流和输入流进行连接，否则在使用时会抛出IOException*/
        out.connect(in);
        Thread printThread = new Thread(new Print(in), "PrintThread");
        printThread.start();
        int receive = 0;
        try {
            /*将键盘的输入，用输出流接受，在实际的业务中，可以将文件流导给输出流*/
            while ((receive = System.in.read()) != -1){
                out.write(receive);
            }
        } finally {
            out.close();
        }
    }

    static class Print implements Runnable {
        private PipedReader in;
        public Print(PipedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            int receive = 0;
            try {
                /*输入流从输出流接收数据，并在控制台显示
                *在实际的业务中，可以将输入流直接通过网络通信写出 */
                while ((receive = in.read()) != -1){
                    System.out.print((char) receive);
                }
            } catch (IOException ex) {
            }
        }
    }
}
