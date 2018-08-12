define({ "api": [
  {
    "type": "POST",
    "url": "project",
    "title": "插入一项目的信息",
    "description": "<p>插入一个项目的信息</p>",
    "group": "PROJECT_ADD",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "companyId",
            "description": "<p>公司id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectId",
            "description": "<p>项目id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectName",
            "description": "<p>项目名称</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectUrl",
            "description": "<p>项目地址</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "onlineTime",
            "description": "<p>在线时间</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\": null\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_ADD",
    "name": "PostProject"
  },
  {
    "type": "DELETE",
    "url": "project",
    "title": "通过companyId & projectId",
    "description": "<p>通过companyId &amp; projectId删除一个项目的信息</p>",
    "group": "PROJECT_DELETE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "companyId",
            "description": "<p>公司id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectId",
            "description": "<p>项目id</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\": null\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_DELETE",
    "name": "DeleteProject"
  },
  {
    "type": "GET",
    "url": "project",
    "title": "通过companyId & projectId得到项目的信息",
    "description": "<p>通过companyId &amp; projectId得到项目的信息</p>",
    "group": "PROJECT_QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "companyId",
            "description": "<p>公司id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectId",
            "description": "<p>项目id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectName",
            "description": "<p>项目名称</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n\t                \"companyId\": \"lalalala\",\n             \t\"onlineTime\": \"2018-08-08\",\n\t                \"projectId\": 12,\n\t                \"projectUrl\": \"不晓得\"\n                 },{\n      \t                \"companyId\": \"lalalala\",\n                   \t\"onlineTime\": \"2018-08-08\",\n      \t                \"projectId\": 12,\n      \t                \"projectUrl\": \"不晓得\"\n                    }\n         }\n      ]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_QUERY",
    "name": "GetProject"
  },
  {
    "type": "PUT",
    "url": "project",
    "title": "通过companyId & projectId更新项目的信息",
    "description": "<p>通过companyId &amp; projectId更新项目的信息，同时返回更新后的信息</p>",
    "group": "PROJECT_UPDATE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "companyId",
            "description": "<p>公司id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectId",
            "description": "<p>项目id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectName",
            "description": "<p>项目名称</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "projectUrl",
            "description": "<p>项目地址</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "onlineTime",
            "description": "<p>在线时间</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":{\n\t                \"companyId\": \"lalalala\",\n             \t\"onlineTime\": \"2018-08-08\",\n\t                \"projectId\": 12,\n\t                \"projectUrl\": \"不晓得\"\n                 }\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_UPDATE",
    "name": "PutProject"
  }
] });