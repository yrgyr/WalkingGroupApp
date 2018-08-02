package ca.cmpt276.walkinggroup.dataobjects;

public class MyBgs {



    private int bgResId;
    private String name;
    private int price;
    private boolean purchased = false;


    public MyBgs(int bgResId,String name,int price) {
        this.bgResId = bgResId;
        this.name = name;
        this.price = price;
    }
    public int getBgResId() {
        return bgResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setAsPurchased(){this.purchased = true;}

    public boolean getPurchasedStatus(){return purchased;}
}
