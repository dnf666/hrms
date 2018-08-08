define({ "api": [
  {
    "type": "POST",
    "url": "book",
    "title": "插入一书本的信息",
    "description": "<p>插入一本书的信息</p>",
    "group": "ADD",
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
            "field": "bookName",
            "description": "<p>书名</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "category",
            "description": "<p>类别</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "quantity",
            "description": "<p>数量</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "version",
            "description": "<p>版本</p>"
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
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "ADD",
    "name": "PostBook"
  },
  {
    "type": "DELETE",
    "url": "book",
    "title": "通过bookId",
    "description": "<p>通过bookId删除一本书的信息</p>",
    "group": "DELETE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "bookId",
            "description": "<p>书的id</p>"
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
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "DELETE",
    "name": "DeleteBook"
  },
  {
    "type": "GET",
    "url": "book-list-1",
    "title": "通过公司id",
    "description": "<p>通过公司id得到书录</p>",
    "group": "QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "companyId",
            "description": "<p>公司id</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n\t            \"bookId\": \"dddddfffffsssss\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n\t            \"category\": \"思维\",\n\t            \"companyId\": \"信管工作室\",\n\t            \"quantity\": 2,\n\t            \"version\": \"2.0\"\n             }, {\n\t            \"bookId\": \"reerrrrrrr\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n         \t\"category\": \"思维\",\n         \t\"companyId\": \"信管工作室\",\n          \t\"quantity\": 2,\n          \t\"version\": \"2.0\"\n             }]\n             }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "QUERY",
    "name": "GetBookList1"
  },
  {
    "type": "GET",
    "url": "book-list-2",
    "title": "通过公司id和类别",
    "description": "<p>通过公司id和分类得到书录</p>",
    "group": "QUERY",
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
            "field": "category",
            "description": "<p>分类</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n\t            \"bookId\": \"dddddfffffsssss\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n\t            \"category\": \"思维\",\n\t            \"companyId\": \"信管工作室\",\n\t            \"quantity\": 2,\n\t            \"version\": \"2.0\"\n             }, {\n\t            \"bookId\": \"reerrrrrrr\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n         \t\"category\": \"思维\",\n         \t\"companyId\": \"信管工作室\",\n          \t\"quantity\": 2,\n          \t\"version\": \"2.0\"\n             }]\n             }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "QUERY",
    "name": "GetBookList2"
  },
  {
    "type": "GET",
    "url": "book-list-3",
    "title": "通过公司id和书名",
    "description": "<p>通过公司id和书名得到书录</p>",
    "group": "QUERY",
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
            "field": "bookName",
            "description": "<p>书的名称</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n\t            \"bookId\": \"dddddfffffsssss\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n\t            \"category\": \"思维\",\n\t            \"companyId\": \"信管工作室\",\n\t            \"quantity\": 2,\n\t            \"version\": \"2.0\"\n             }, {\n\t            \"bookId\": \"reerrrrrrr\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n         \t\"category\": \"思维\",\n         \t\"companyId\": \"信管工作室\",\n          \t\"quantity\": 2,\n          \t\"version\": \"2.0\"\n             }]\n             }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "QUERY",
    "name": "GetBookList3"
  },
  {
    "type": "GET",
    "url": "book-list-4",
    "title": "通过书的id",
    "description": "<p>通过书的id得到书的信息</p>",
    "group": "QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "bookId",
            "description": "<p>书的id</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":{\n\t            \"bookId\": \"dddddfffffsssss\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n\t            \"category\": \"思维\",\n\t            \"companyId\": \"信管工作室\",\n\t            \"quantity\": 2,\n\t            \"version\": \"2.0\"\n             }\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "QUERY",
    "name": "GetBookList4"
  },
  {
    "type": "PUT",
    "url": "book",
    "title": "通过书的id更新书的信息",
    "description": "<p>通过书的id更新书的信息，同时返回更新后的信息</p>",
    "group": "UPDATE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "bookId",
            "description": "<p>书的id</p>"
          },
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
            "field": "bookName",
            "description": "<p>书名</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "category",
            "description": "<p>类别</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "quantity",
            "description": "<p>数量</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "version",
            "description": "<p>版本</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":{\n\t            \"bookId\": \"dddddfffffsssss\",\n\t            \"bookName\": \"你的灯亮着吗？\",\n\t            \"category\": \"思维\",\n\t            \"companyId\": \"信管工作室\",\n\t            \"quantity\": 2,\n\t            \"version\": \"2.0\"\n             }\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/book/controller/BookController.java",
    "groupTitle": "UPDATE",
    "name": "PutBook"
  }
] });
