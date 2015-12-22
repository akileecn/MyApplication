package cn.aki.mobilesafe.bean;

/**
 * Created by Administrator on 2015/12/22.
 * 黑名单实体
 */
public class BlackNumber {
    public static final int MODE_PHONE=1;
    public static final int MODE_SMS=2;
    public static final int MODE_ALL=3;
    //手机号码
    private String number;
    //拦截模式
    private Integer mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    /**
     * 获得拦截模式名称
     * @return 模式名称
     */
    public String getModeName(){
        switch (mode){
            case MODE_PHONE:
                return "电话拦截";
            case MODE_SMS:
                return "短信拦截";
            case MODE_ALL:
                return "电话短信拦截";
            default:
                return "";
        }
    }
}
