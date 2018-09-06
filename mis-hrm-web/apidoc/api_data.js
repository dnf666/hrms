define({ "api": [
  {
    "type": "POST",
    "url": "bookLendInfo",
    "title": "插入借一本书的信息",
    "description": "<p>插入一本借书的信息</p>",
    "group": "BOOKLEND_ADD",
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
            "field": "lendTime",
            "description": "<p>借书时间</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "returnTime",
            "description": "<p>归还时间（可以为空）</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "borrower",
            "description": "<p>借书者</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_ADD",
    "name": "PostBooklendinfo"
  },
  {
    "type": "DELETE",
    "url": "bookLendInfo/{companyId}/{bookRecord}",
    "title": "通过companyId & bookRecord 删除借书信息",
    "description": "<p>通过companyId &amp; bookRecord 删除借书信息</p>",
    "group": "BOOKLEND_DELETE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "companyId",
            "description": "<p>公司的id</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "bookRecord",
            "description": "<p>借书记录</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_DELETE",
    "name": "DeleteBooklendinfoCompanyidBookrecord"
  },
  {
    "type": "GET",
    "url": "booklend-list-５",
    "title": "得到所有借书信息",
    "description": "<p>得到所有的借书信息</p>",
    "group": "BOOKLEND_QUERY",
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n    \t   \"bookName\": \"暗时间\",\n\t       \"bookRecord\": \"不晓得是撒子啊\",\n\t       \"borrower\": \"优秀的人\",\n\t       \"companyId\": \"信管工作室\",\n\t       \"lendTime\": \"2018-08-08\",\n\t       \"returnTime\": \"2018-08-08\"\n         },{\n\t        \"bookName\": \"暗时间\",\n\t        \"bookRecord\": \"不晓得是撒子啊\",\n\t        \"borrower\": \"优秀的人\",\n\t        \"companyId\": \"信管工作室\",\n\t        \"lendTime\": \"2018-08-08\",\n\t        \"returnTime\": \"2018-08-08\"\n        }]\n\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_QUERY",
    "name": "GetBooklendList"
  },
  {
    "type": "GET",
    "url": "booklend-list-1",
    "title": "通过借书者",
    "description": "<p>通过借书者查询借书信息</p>",
    "group": "BOOKLEND_QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "borrower",
            "description": "<p>借书者</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n    \t   \"bookName\": \"暗时间\",\n\t       \"bookRecord\": \"不晓得是撒子啊\",\n\t       \"borrower\": \"优秀的人\",\n\t       \"companyId\": \"信管工作室\",\n\t       \"lendTime\": \"2018-08-08\",\n\t       \"returnTime\": \"2018-08-08\"\n         },{\n\t        \"bookName\": \"暗时间\",\n\t        \"bookRecord\": \"不晓得是撒子啊\",\n\t        \"borrower\": \"优秀的人\",\n\t        \"companyId\": \"信管工作室\",\n\t        \"lendTime\": \"2018-08-08\",\n\t        \"returnTime\": \"2018-08-08\"\n        }]\n\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_QUERY",
    "name": "GetBooklendList1"
  },
  {
    "type": "GET",
    "url": "booklend-list-2",
    "title": "通过公司id查询借书的信息",
    "description": "<p>通过公司id查询借书信息</p>",
    "group": "BOOKLEND_QUERY",
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
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n    \t   \"bookName\": \"暗时间\",\n\t       \"bookRecord\": \"不晓得是撒子啊\",\n\t       \"borrower\": \"优秀的人\",\n\t       \"companyId\": \"信管工作室\",\n\t       \"lendTime\": \"2018-08-08\",\n\t       \"returnTime\": \"2018-08-08\"\n         },{\n\t        \"bookName\": \"暗时间\",\n\t        \"bookRecord\": \"不晓得是撒子啊\",\n\t        \"borrower\": \"优秀的人\",\n\t        \"companyId\": \"信管工作室\",\n\t        \"lendTime\": \"2018-08-08\",\n\t        \"returnTime\": \"2018-08-08\"\n        }]\n\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_QUERY",
    "name": "GetBooklendList2"
  },
  {
    "type": "GET",
    "url": "booklend-list-3",
    "title": "通过公司和书名",
    "description": "<p>通过公司和书名查询借书信息</p>",
    "group": "BOOKLEND_QUERY",
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
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":[{\n    \t   \"bookName\": \"暗时间\",\n\t       \"bookRecord\": \"不晓得是撒子啊\",\n\t       \"borrower\": \"优秀的人\",\n\t       \"companyId\": \"信管工作室\",\n\t       \"lendTime\": \"2018-08-08\",\n\t       \"returnTime\": \"2018-08-08\"\n         },{\n\t        \"bookName\": \"暗时间\",\n\t        \"bookRecord\": \"不晓得是撒子啊\",\n\t        \"borrower\": \"优秀的人\",\n\t        \"companyId\": \"信管工作室\",\n\t        \"lendTime\": \"2018-08-08\",\n\t        \"returnTime\": \"2018-08-08\"\n        }]\n\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_QUERY",
    "name": "GetBooklendList3"
  },
  {
    "type": "GET",
    "url": "booklend-list-4",
    "title": "通过公司名和借书的记录",
    "description": "<p>通过公司名和书的记录查询借书信息</p>",
    "group": "BOOKLEND_QUERY",
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
            "field": "bookRecord",
            "description": "<p>借书记录</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "      HTTP/1.1 200 OK\n      {\n        \"code\": \"1\",\n        \"msg\": \"success\"\n        \"object\":{\n    \t   \"bookName\": \"暗时间\",\n\t       \"bookRecord\": \"不晓得是撒子啊\",\n\t       \"borrower\": \"优秀的人\",\n\t       \"companyId\": \"信管工作室\",\n\t       \"lendTime\": \"2018-08-08\",\n\t       \"returnTime\": \"2018-08-08\"\n         }\n      }",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_QUERY",
    "name": "GetBooklendList4"
  },
  {
    "type": "PUT",
    "url": "bookLendInfo",
    "title": "通过companyId & bookRecord 更改借书信息",
    "description": "<p>通过companyId &amp; bookRecord 更改借书信息，同时返回更新后的信息</p>",
    "group": "BOOKLEND_UPDATE",
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
            "field": "bookRecord",
            "description": "<p>借书记录</p>"
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
            "field": "lendTime",
            "description": "<p>借书时间</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "returnTime",
            "description": "<p>归还时间</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "borrower",
            "description": "<p>借书者</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":{\n   \t   \"bookName\": \"暗时间\",\n\t       \"bookRecord\": \"不晓得是撒子啊\",\n\t       \"borrower\": \"优秀的人\",\n\t       \"companyId\": \"信管工作室\",\n\t       \"lendTime\": \"2018-08-08\",\n\t       \"returnTime\": \"2018-08-08\"\n        }\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookLendController.java",
    "groupTitle": "BOOKLEND_UPDATE",
    "name": "PutBooklendinfo"
  },
  {
    "type": "POST",
    "url": "book",
    "title": "插入一书本的信息",
    "description": "<p>插入一本书的信息</p>",
    "group": "BOOK_ADD",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_ADD",
    "name": "PostBook"
  },
  {
    "type": "DELETE",
    "url": "book/{bookId}",
    "title": "通过bookId",
    "description": "<p>通过bookId删除一本书的信息</p>",
    "group": "BOOK_DELETE",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_DELETE",
    "name": "DeleteBookBookid"
  },
  {
    "type": "GET",
    "url": "book-list-1",
    "title": "通过公司id",
    "description": "<p>通过公司id得到书录</p>",
    "group": "BOOK_QUERY",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_QUERY",
    "name": "GetBookList1"
  },
  {
    "type": "GET",
    "url": "book-list-2",
    "title": "通过公司id和类别",
    "description": "<p>通过公司id和分类得到书录</p>",
    "group": "BOOK_QUERY",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_QUERY",
    "name": "GetBookList2"
  },
  {
    "type": "GET",
    "url": "book-list-3",
    "title": "通过公司id和书名",
    "description": "<p>通过公司id和书名得到书录</p>",
    "group": "BOOK_QUERY",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_QUERY",
    "name": "GetBookList3"
  },
  {
    "type": "GET",
    "url": "book-list-4",
    "title": "通过书的id",
    "description": "<p>通过书的id得到书的信息</p>",
    "group": "BOOK_QUERY",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_QUERY",
    "name": "GetBookList4"
  },
  {
    "type": "PUT",
    "url": "book",
    "title": "通过书的id更新书的信息",
    "description": "<p>通过书的id更新书的信息，同时返回更新后的信息</p>",
    "group": "BOOK_UPDATE",
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
    "filename": "src/main/java/com/mis/hrm/web/book/controller/BookController.java",
    "groupTitle": "BOOK_UPDATE",
    "name": "PutBook"
  },
  {
    "type": "GET",
    "url": "{tableTitle}/download",
    "title": "模板下载",
    "description": "<p>tableTitle填member或whereabout；body里的是Excel模板的字节流</p>",
    "group": "EXCEL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "tableTitle",
            "description": "<p>数据库表名</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"msg\": \"success\",\n  \"code\": 1,\n  \"object\": {\n      \"headers\": {\n           \"Content-Type\": [\n               \"application/octet-stream\"\n           ],\n           \"Content-Disposition\": [\n               \"form-data; name=\\\"attachment\\\"; filename=\\\"model-member-1536216152560.xlsx\\\"\"\n           ]\n      },\n      \"body\": \"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\",\n      \"statusCode\": \"CREATED\",\n      \"statusCodeValue\": 201\n  }\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/excel/apidoc/ExcelApiDoc.java",
    "groupTitle": "EXCEL",
    "name": "GetTabletitleDownload"
  },
  {
    "type": "GET",
    "url": "{tableTitle}/fromExcel",
    "title": "将数据从Excel导出到数据库",
    "description": "<p>其实这个tableTitle可以瞎填，有它只是为了保持格式一致，但最好还是写member或whereabout啦</p>",
    "group": "EXCEL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "MultipartFile",
            "optional": false,
            "field": "file",
            "description": "<p>用户上传的Excel文件</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/excel/apidoc/ExcelApiDoc.java",
    "groupTitle": "EXCEL",
    "name": "GetTabletitleFromexcel"
  },
  {
    "type": "GET",
    "url": "{tableTitle}/toExcel",
    "title": "将数据从数据库导入到Excel",
    "description": "<p>tableTitle填member或whereabout；body里的是Excel文件的字节流</p>",
    "group": "EXCEL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "tableTitle",
            "description": "<p>数据库表名</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\nHTTP/1.1 200 OK\n{\n  \"msg\": \"success\",\n  \"code\": 1,\n  \"object\": {\n      \"headers\": {\n           \"Content-Type\": [\n               \"application/octet-stream\"\n           ],\n           \"Content-Disposition\": [\n               \"form-data; name=\\\"attachment\\\"; filename=\\\"model-member-1536216152560.xlsx\\\"\"\n           ]\n      },\n      \"body\": \"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\",\n      \"statusCode\": \"CREATED\",\n      \"statusCodeValue\": 201\n  }\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/excel/apidoc/ExcelApiDoc.java",
    "groupTitle": "EXCEL",
    "name": "GetTabletitleToexcel"
  },
  {
    "type": "POST",
    "url": "member",
    "title": "添加单个成员信息",
    "group": "MEMBER_ADD",
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
            "field": "num",
            "description": "<p>学号</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>电话</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>邮箱</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "grade",
            "description": "<p>年级（如2017级）</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "sex",
            "description": "<p>性别</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "profession",
            "description": "<p>专业</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "department",
            "description": "<p>部门</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_ADD",
    "name": "PostMember"
  },
  {
    "type": "DELETE",
    "url": "member",
    "title": "(批量)删除成员信息",
    "description": "<p>根据num组删除成员信息，返回成功删除的成员个数</p>",
    "group": "MEMBER_DELETE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "List",
            "optional": false,
            "field": "nums",
            "description": "<p>学号</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\": 3\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_DELETE",
    "name": "DeleteMember"
  },
  {
    "type": "GET",
    "url": "member/all",
    "title": "获取成员列表",
    "description": "<p>分页获取全部成员信息</p>",
    "group": "MEMBER_QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "page",
            "description": "<p>当前页码</p>"
          },
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "size",
            "description": "<p>每页数量</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":[{\n      \"companyId\": \"信管工作室\",\n      \"num\": \"001\",\n      \"name\": \"大红\",\n      \"phoneNumber\": \"21212222222\",\n      \"email\": \"211@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\"\n  },\n  {\n      \"companyId\": \"信管工作室\",\n      \"num\": \"002\",\n      \"name\": \"大白\",\n      \"phoneNumber\": \"21212333333\",\n      \"email\": \"222@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\"\n  }]\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_QUERY",
    "name": "GetMemberAll"
  },
  {
    "type": "GET",
    "url": "member/count",
    "title": "获取成员总数",
    "description": "<p>直接返回成员总数</p>",
    "group": "MEMBER_QUERY",
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\": 12\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_QUERY",
    "name": "GetMemberCount"
  },
  {
    "type": "POST",
    "url": "member/filter",
    "title": "筛选成员信息",
    "description": "<p>根据表单数据筛选成员信息</p>",
    "group": "MEMBER_QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "page",
            "description": "<p>当前页码</p>"
          },
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "size",
            "description": "<p>每页数量</p>"
          },
          {
            "group": "Parameter",
            "type": "Member",
            "optional": false,
            "field": "member",
            "description": "<p>表单获取到的成员信息</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":[{\n      \"companyId\": \"信管工作室\",\n      \"num\": \"001\",\n      \"name\": \"大红\",\n      \"phoneNumber\": \"21212222222\",\n      \"email\": \"211@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\"\n  },\n  {\n      \"companyId\": \"信管工作室\",\n      \"num\": \"002\",\n      \"name\": \"大白\",\n      \"phoneNumber\": \"21212333333\",\n      \"email\": \"222@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\"\n  }]\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_QUERY",
    "name": "PostMemberFilter"
  },
  {
    "type": "PUT",
    "url": "member",
    "title": "更新单个成员信息",
    "description": "<p>根据companyId和num更新成员信息</p>",
    "group": "MEMBER_UPDATE",
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
            "field": "num",
            "description": "<p>学号</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>电话</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>邮箱</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "grade",
            "description": "<p>年级（如2017级）</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "sex",
            "description": "<p>性别</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "profession",
            "description": "<p>专业</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "department",
            "description": "<p>部门</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_UPDATE",
    "name": "PutMember"
  },
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_ADD",
    "name": "PostProject"
  },
  {
    "type": "DELETE",
    "url": "project/{companyId}/{projectId}",
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_DELETE",
    "name": "DeleteProjectCompanyidProjectid"
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_QUERY",
    "name": "GetProject"
  },
  {
    "type": "PUT",
    "url": "project",
    "title": "通过companyId & projectI更新项目的信息",
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_UPDATE",
    "name": "PutProject"
  },
  {
    "type": "POST",
    "url": "work",
    "title": "添加单个成员信息",
    "group": "WORK_ADD",
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
            "field": "num",
            "description": "<p>学号</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>电话</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>邮箱</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "grade",
            "description": "<p>年级（如2017级）</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "sex",
            "description": "<p>性别</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "profession",
            "description": "<p>专业</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "department",
            "description": "<p>部门</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "workPlace",
            "description": "<p>工作地点</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_ADD",
    "name": "PostWork"
  },
  {
    "type": "DELETE",
    "url": "work",
    "title": "(批量)删除成员信息",
    "description": "<p>根据num组删除成员信息，返回成功删除的成员个数</p>",
    "group": "WORK_DELETE",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "List",
            "optional": false,
            "field": "nums",
            "description": "<p>学号</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\": 3\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_DELETE",
    "name": "DeleteWork"
  },
  {
    "type": "GET",
    "url": "work/all",
    "title": "获取成员列表",
    "description": "<p>分页获取全部成员信息</p>",
    "group": "WORK_QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "page",
            "description": "<p>当前页码</p>"
          },
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "size",
            "description": "<p>每页数量</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":[{\n      \"companyId\": \"信管工作室\",\n      \"num\": \"001\",\n      \"name\": \"大红\",\n      \"phoneNumber\": \"21212222222\",\n      \"email\": \"211@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\",\n      \"workPlace\": \"小米\"\n  },\n  {\n      \"companyId\": \"信管工作室\",\n      \"num\": \"002\",\n      \"name\": \"大白\",\n      \"phoneNumber\": \"21212333333\",\n      \"email\": \"222@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\",\n      \"workPlace\": \"小米\"\n  }]\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_QUERY",
    "name": "GetWorkAll"
  },
  {
    "type": "GET",
    "url": "work/count",
    "title": "获取成员总数",
    "description": "<p>直接返回成员总数</p>",
    "group": "WORK_QUERY",
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\": 12\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_QUERY",
    "name": "GetWorkCount"
  },
  {
    "type": "POST",
    "url": "work/filter",
    "title": "筛选成员信息",
    "description": "<p>根据表单数据筛选成员信息</p>",
    "group": "WORK_QUERY",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "page",
            "description": "<p>当前页码</p>"
          },
          {
            "group": "Parameter",
            "type": "Integer",
            "optional": false,
            "field": "size",
            "description": "<p>每页数量</p>"
          },
          {
            "group": "Parameter",
            "type": "Whereabout",
            "optional": false,
            "field": "whereabout",
            "description": "<p>表单获取到的成员信息</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":[{\n      \"companyId\": \"信管工作室\",\n      \"num\": \"001\",\n      \"name\": \"大红\",\n      \"phoneNumber\": \"21212222222\",\n      \"email\": \"211@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\",\n      \"workPlace\": \"小米\"\n  },\n  {\n      \"companyId\": \"信管工作室\",\n      \"num\": \"002\",\n      \"name\": \"大白\",\n      \"phoneNumber\": \"21212333333\",\n      \"email\": \"222@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\",\n      \"workPlace\": \"小米\"\n  }]\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_QUERY",
    "name": "PostWorkFilter"
  },
  {
    "type": "PUT",
    "url": "work",
    "title": "更新单个成员信息",
    "description": "<p>根据companyId和num更新成员信息</p>",
    "group": "WORK_UPDATE",
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
            "field": "num",
            "description": "<p>学号</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>电话</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>邮箱</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "grade",
            "description": "<p>年级（如2017级）</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "sex",
            "description": "<p>性别</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "profession",
            "description": "<p>专业</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "department",
            "description": "<p>部门</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "workPlace",
            "description": "<p>工作地点</p>"
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
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_UPDATE",
    "name": "PutWork"
  }
] });
