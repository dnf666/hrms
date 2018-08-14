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
    "url": "bookLendInfo",
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
    "name": "DeleteBooklendinfo"
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
    "url": "book",
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
    "name": "DeleteBook"
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
    "url": "hrms/{tableTitle}/fromExcel",
    "title": "将数据从Excel导出到数据库",
    "description": "<p>其实这个tableTitle可以瞎填，有它只是为了保持格式一致，但最好还是写member或whereabout啦</p>",
    "group": "EXCEL",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "filePath",
            "description": "<p>Excel文件的具体路径</p>"
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
    "name": "GetHrmsTabletitleFromexcel"
  },
  {
    "type": "GET",
    "url": "hrms/{tableTitle}/toExcel",
    "title": "将数据从数据库导入到Excel",
    "description": "<p>目前可填的tableTitle只有member和whereabout啦</p>",
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
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "filePath",
            "description": "<p>Excel文件的具体路径</p>"
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
    "name": "GetHrmsTabletitleToexcel"
  },
  {
    "type": "POST",
    "url": "hrms/member",
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
    "name": "PostHrmsMember"
  },
  {
    "type": "DELETE",
    "url": "hrms/member",
    "title": "删除单个成员信息",
    "description": "<p>根据companyId和num删除成员信息</p>",
    "group": "MEMBER_DELETE",
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
    "groupTitle": "MEMBER_DELETE",
    "name": "DeleteHrmsMember"
  },
  {
    "type": "GET",
    "url": "hrms/member",
    "title": "查找单个成员信息",
    "description": "<p>根据companyId和num查找成员信息</p>",
    "group": "MEMBER_QUERY",
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
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":{\n      \"companyId\": \"信管工作室\",\n      \"num\": \"001\",\n      \"name\": \"大红\",\n      \"phoneNumber\": \"21212222222\",\n      \"email\": \"211@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\"\n  }\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/member/apidoc/MemberApiDoc.java",
    "groupTitle": "MEMBER_QUERY",
    "name": "GetHrmsMember"
  },
  {
    "type": "GET",
    "url": "hrms/member/all/{page}",
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
    "name": "GetHrmsMemberAllPage"
  },
  {
    "type": "GET",
    "url": "hrms/member/byEmail/{page}",
    "title": "根据邮箱获取成员",
    "description": "<p>根据邮箱的模糊分页查询</p>",
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
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>邮箱</p>"
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
    "name": "GetHrmsMemberByemailPage"
  },
  {
    "type": "GET",
    "url": "hrms/member/byName/{page}",
    "title": "根据姓名获取成员",
    "description": "<p>根据姓名的模糊分页查询</p>",
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
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
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
    "name": "GetHrmsMemberBynamePage"
  },
  {
    "type": "GET",
    "url": "hrms/member/byPhone/{page}",
    "title": "根据电话获取成员",
    "description": "<p>根据电话的模糊分页查询</p>",
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
            "type": "String",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>电话</p>"
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
    "name": "GetHrmsMemberByphonePage"
  },
  {
    "type": "GET",
    "url": "hrms/member/count",
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
    "name": "GetHrmsMemberCount"
  },
  {
    "type": "PUT",
    "url": "hrms/member",
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
    "name": "PutHrmsMember"
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
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
    "filename": "src/main/java/com/mis/hrm/web/project/controller/ProjectController.java",
    "groupTitle": "PROJECT_UPDATE",
    "name": "PutProject"
  },
  {
    "type": "POST",
    "url": "hrms/work",
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
    "name": "PostHrmsWork"
  },
  {
    "type": "DELETE",
    "url": "hrms/work",
    "title": "删除单个成员信息",
    "description": "<p>根据companyId和num删除成员信息</p>",
    "group": "WORK_DELETE",
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
    "groupTitle": "WORK_DELETE",
    "name": "DeleteHrmsWork"
  },
  {
    "type": "GET",
    "url": "hrms/work",
    "title": "查找单个成员信息",
    "description": "<p>根据companyId和num查找成员信息</p>",
    "group": "WORK_QUERY",
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
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"code\": \"1\",\n  \"msg\": \"success\"\n  \"object\":{\n      \"companyId\": \"信管工作室\",\n      \"num\": \"001\",\n      \"name\": \"大红\",\n      \"phoneNumber\": \"21212222222\",\n      \"email\": \"211@222.com\",\n      \"grade\": \"2017级\",\n      \"sex\": \"女\",\n      \"profession\": \"信管\",\n      \"department\": \"后台\",\n      \"workPlace\": \"小米\"\n  }\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/mis/hrm/web/work/apidoc/WorkApiDoc.java",
    "groupTitle": "WORK_QUERY",
    "name": "GetHrmsWork"
  },
  {
    "type": "GET",
    "url": "hrms/work/all/{page}",
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
    "name": "GetHrmsWorkAllPage"
  },
  {
    "type": "GET",
    "url": "hrms/work/byGrade/{page}",
    "title": "根据年级获取成员",
    "description": "<p>根据年级的分页查询（注意这里没有模糊查询）</p>",
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
            "type": "String",
            "optional": false,
            "field": "grade",
            "description": "<p>年级（如2017级）</p>"
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
    "name": "GetHrmsWorkBygradePage"
  },
  {
    "type": "GET",
    "url": "hrms/work/byName/{page}",
    "title": "根据姓名获取成员",
    "description": "<p>根据姓名的模糊分页查询</p>",
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
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>姓名</p>"
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
    "name": "GetHrmsWorkBynamePage"
  },
  {
    "type": "GET",
    "url": "hrms/work/count",
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
    "name": "GetHrmsWorkCount"
  },
  {
    "type": "PUT",
    "url": "hrms/work",
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
    "name": "PutHrmsWork"
  }
] });