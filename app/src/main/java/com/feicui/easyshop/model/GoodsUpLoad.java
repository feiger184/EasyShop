package com.feicui.easyshop.model;

/**
 *商品上传实体类
 */

public class GoodsUpLoad {

   /* {
        "description": "诚信商家，非诚勿扰",     //商品描述
            "master": "android",                    //商品发布者
            "name": "礼物，鱼丸，鱼翅，火箭，飞机",   //商品名称
            "price": "88",                          //商品价格
            "type": "gift"                          //商品类型
    }*/

    /*商品名称*/
    private String name;
    /*商品价格*/
    private String price;
    /*商品描述*/
    private String description;
    /*商品类型*/
    private String type;
    /*商品发布人*/
    private String master;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescribe(String describe) {
        this.description = describe;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
