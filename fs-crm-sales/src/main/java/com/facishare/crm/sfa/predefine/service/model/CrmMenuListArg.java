package com.facishare.crm.sfa.predefine.service.model;

import com.google.common.collect.Lists;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import lombok.Builder;
import lombok.Data;

public interface CrmMenuListArg {
    @Data
    @Builder
    class Result {
        @JSONField(name = "M1")
        @Builder.Default
        private List<Menu> menus = Lists.newLinkedList();


        @JSONField(name = "M2")
        private Object configinfo;

        private Mobile mobile;
    }

    @Data
    class Menu {
        //菜单id
        @JSONField(name = "M1")
        private String id;

        @JSONField(name = "M2")
        private String displayName;

        @JSONField(name = "M3")
        private List<MenuItem> items;

        //是否系统预制CRM菜单
        @JSONField(name = "M4")
        private Boolean isSystem = false;

        //是否当前客户默认选择
        @JSONField(name = "M5")
        private Boolean isCurrent = false;
    }

    @Data
    @Builder
    class MenuItem {
        //个人工作台的id和pid值,主要用于区分父子关系
        @JSONField(name = "M1")
        private String id;

        @JSONField(name = "M2")
        private String pid;

        @JSONField(name = "M3")
        private String type;

        @JSONField(name = "M4")
        private String url;

        //菜单项id
        @JSONField(name = "M5")
        private String menuItemId;

        @JSONField(name = "M6")
        private String menuId;

        @JSONField(name = "M7")
        private String displayName;

        @JSONField(name = "M8")
        private String referenceApiname;

        @JSONField(name = "M9")
        private Integer iconIndex;

        @JSONField(name = "M10")
        private String iconPathHome;

        @JSONField(name = "M11")
        private List<String> privilegeAction;

        @JSONField(name = "M12")
        private Boolean isHidden;

        @JSONField(name = "M13")
        private Integer number;

        @JSONField(name = "M14")
        private String iconPathMenu;
        @JSONField(name = "M15")
        private MobileConfig mobileConfig;
    }

    @Data
    class MobileConfig {
        @JSONField(name = "M1")
        private String ListAction;
        @JSONField(name = "M2")
        private String addAction;

    }

    class Mobile {
        private String logo;
        private String style;
    }
}
