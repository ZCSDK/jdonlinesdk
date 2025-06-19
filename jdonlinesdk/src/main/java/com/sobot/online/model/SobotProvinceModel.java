package com.sobot.online.model;

import com.sobot.online.weight.contrarywind.interfaces.IPickerViewData;

import java.util.List;

//省市区 联动 实体类
public class SobotProvinceModel
        implements IPickerViewData {
    //    {
//        "areaId": "110000",
//            "areaName": "北京市",
//            "cities": [
//        {
//            "areaId": "110000",
//                "areaName": "市辖区",
//                "counties": [
//            {
//                "areaId": "110101",
//                    "areaName": "东城区"
//            },
//            {
//                "areaId": "110102",
//                    "areaName": "西城区"
//            },
//            {
//                "areaId": "110105",
//                    "areaName": "朝阳区"
//            },
//            {
//                "areaId": "110106",
//                    "areaName": "丰台区"
//            },
//            {
//                "areaId": "110107",
//                    "areaName": "石景山区"
//            },
//            {
//                "areaId": "110108",
//                    "areaName": "海淀区"
//            },
//            {
//                "areaId": "110109",
//                    "areaName": "门头沟区"
//            },
//            {
//                "areaId": "110111",
//                    "areaName": "房山区"
//            },
//            {
//                "areaId": "110112",
//                    "areaName": "通州区"
//            },
//            {
//                "areaId": "110113",
//                    "areaName": "顺义区"
//            },
//            {
//                "areaId": "110114",
//                    "areaName": "昌平区"
//            },
//            {
//                "areaId": "110115",
//                    "areaName": "大兴区"
//            },
//            {
//                "areaId": "110116",
//                    "areaName": "怀柔区"
//            },
//            {
//                "areaId": "110117",
//                    "areaName": "平谷区"
//            },
//            {
//                "areaId": "110228",
//                    "areaName": "密云县"
//            },
//            {
//                "areaId": "110229",
//                    "areaName": "延庆县"
//            }
//        ]
//        }
//    ]
//    }
    private String areaId;
    private String areaName;
    private List<CityBean> cities;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public List<CityBean> getCities() {
        return cities;
    }

    public void setCities(List<CityBean> cities) {
        this.cities = cities;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.areaName;
    }


    public static class CityBean implements IPickerViewData {
        /**
         * name : 110000
         * area : 市辖区
         */

        private String areaId;
        private String areaName;
        private List<AreaBean> counties;

        @Override
        public String getPickerViewText() {
            return areaName;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public List<AreaBean> getCounties() {
            return counties;
        }

        public void setCounties(List<AreaBean> counties) {
            this.counties = counties;
        }
    }

    public static class AreaBean implements IPickerViewData {

        private String areaId;
        private String areaName;

        @Override
        public String getPickerViewText() {
            return areaName;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }
    }
}
