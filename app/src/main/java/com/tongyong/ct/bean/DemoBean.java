package com.tongyong.ct.bean;

import java.util.List;

/**
 * Created by Chentao on 15/12/24.
 */
public class DemoBean {
    String total;

    List<InnerBean> row;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<InnerBean> getRow() {
        return row;
    }

    public void setRow(List<InnerBean> row) {
        this.row = row;
    }

    public class InnerBean {

        String code;
        String name;
        String addr;
        String col4;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getCol4() {
            return col4;
        }

        public void setCol4(String col4) {
            this.col4 = col4;
        }
    }
}
