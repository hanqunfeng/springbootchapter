package org.example.base.wn.temp;



/**
 * 枪
 */
public class GunController {
    public static void main(String[] args) {
        Bullet bullet = new Bullet();
        Thread inputThread = new Thread(new Input(bullet));
        Thread outputThread = new Thread(new Output(bullet));
        inputThread.setName("装弹线程");
        outputThread.setName("射击线程");
        inputThread.setDaemon(true);
        outputThread.setDaemon(true);
        inputThread.start();
        outputThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        inputThread.interrupt();
        outputThread.interrupt();


    }






}
