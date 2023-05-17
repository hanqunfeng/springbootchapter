package org.example.base.wn;

/**
 *类说明：快递实体类
 */
public class Express {
    public final static String DIST_CITY = "ShangHai";
    public final static int TOTAL = 500;
    private int km ;/*快递运输里程数*/
    private String site;/*快递到达地点*/

    public Express() {
    }

    public Express(int km, String site) {
        this.km = km;
        this.site = site;
    }

    public void change(){
        if (km < TOTAL){
            km = km +100;
            System.out.println("the Km is "+this.km);
        }
        if(km >= TOTAL){
            site = DIST_CITY;
            System.out.println("the Express is arrived");
        }
    }

    /*线程等待公里的变化*/
    public synchronized void waitKm(){
        while(this.km <= TOTAL){
            try {
                wait();
                System.out.println("Map thread["
                                +Thread.currentThread().getId()
                        +"] wake,I will change db");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*线程等待目的地的变化*/
    public synchronized void waitSite(){
        while(!this.site.equals(DIST_CITY)){
            try {
                wait();
                System.out.println("Notice User thread["+Thread.currentThread().getId()
                		+"] wake");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("the site is "+this.site+",I will call user");
    }
}
