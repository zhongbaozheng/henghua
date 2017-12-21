package com.hengdian.henghua.model.viewBean;


public class ItemBean {
    private int img;
    private String title;
    private boolean isTagVisible =false;

    public ItemBean() {

    }

    public ItemBean(int img, String title) {
        this.img = img;
        this.title = title;
    }

    public ItemBean(int img, String title,boolean isTagVisible) {
        this.img = img;
        this.title = title;
        this.isTagVisible = isTagVisible;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTagVisible(boolean isTagVisible) {
        isTagVisible = isTagVisible;
    }

    public boolean isTagVisible() {
        return isTagVisible;
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "img=" + img +
                ", title='" + title + '\'' +
                ", isVisible=" + isTagVisible +
                '}';
    }
}
