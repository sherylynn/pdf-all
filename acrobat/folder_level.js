

if (typeof JSON !== "object") {
    JSON = {};
}
(function () {
    var rx_one = /^[\],:{}\s]*$/;
    var rx_two = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g;
    var rx_three = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g;
    var rx_four = /(?:^|:|,)(?:\s*\[)+/g;
    var rx_escapable = /[\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
    var rx_dangerous = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
    function f(n) {
        return (n < 10)
            ? "0" + n
            : n;
    }
    function this_value() {
        return this.valueOf();
    }
    if (typeof Date.prototype.toJSON !== "function") {
        Date.prototype.toJSON = function () {
            return isFinite(this.valueOf())
                ? (
                    this.getUTCFullYear()
                    + "-"
                    + f(this.getUTCMonth() + 1)
                    + "-"
                    + f(this.getUTCDate())
                    + "T"
                    + f(this.getUTCHours())
                    + ":"
                    + f(this.getUTCMinutes())
                    + ":"
                    + f(this.getUTCSeconds())
                    + "Z"
                )
                : null;
        };
        Boolean.prototype.toJSON = this_value;
        Number.prototype.toJSON = this_value;
        String.prototype.toJSON = this_value;
    }
    var gap;
    var indent;
    var meta;
    var rep;
    function quote(string) {
        rx_escapable.lastIndex = 0;
        return rx_escapable.test(string)
            ? "\"" + string.replace(rx_escapable, function (a) {
                var c = meta[a];
                return typeof c === "string"
                    ? c
                    : "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4);
            }) + "\""
            : "\"" + string + "\"";
    }
    function str(key, holder) {
        var i;          // The loop counter.
        var k;          // The member key.
        var v;          // The member value.
        var length;
        var mind = gap;
        var partial;
        var value = holder[key];
        if (
            value
            && typeof value === "object"
            && typeof value.toJSON === "function"
        ) {
            value = value.toJSON(key);
        }
        if (typeof rep === "function") {
            value = rep.call(holder, key, value);
        }
        switch (typeof value) {
        case "string":
            return quote(value);
        case "number":
            return (isFinite(value))
                ? String(value)
                : "null";
        case "boolean":
        case "null":
            return String(value);
        case "object":
            if (!value) {
                return "null";
            }
            gap += indent;
            partial = [];
            if (Object.prototype.toString.apply(value) === "[object Array]") {
                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || "null";
                }
                v = partial.length === 0
                    ? "[]"
                    : gap
                        ? (
                            "[\n"
                            + gap
                            + partial.join(",\n" + gap)
                            + "\n"
                            + mind
                            + "]"
                        )
                        : "[" + partial.join(",") + "]";
                gap = mind;
                return v;
            }
            if (rep && typeof rep === "object") {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === "string") {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (
                                (gap)
                                    ? ": "
                                    : ":"
                            ) + v);
                        }
                    }
                }
            } else {
                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (
                                (gap)
                                    ? ": "
                                    : ":"
                            ) + v);
                        }
                    }
                }
            }
            v = partial.length === 0
                ? "{}"
                : gap
                    ? "{\n" + gap + partial.join(",\n" + gap) + "\n" + mind + "}"
                    : "{" + partial.join(",") + "}";
            gap = mind;
            return v;
        }
    }
    if (typeof JSON.stringify !== "function") {
        meta = {    // table of character substitutions
            "\b": "\\b",
            "\t": "\\t",
            "\n": "\\n",
            "\f": "\\f",
            "\r": "\\r",
            "\"": "\\\"",
            "\\": "\\\\"
        };
        JSON.stringify = function (value, replacer, space) {
            var i;
            gap = "";
            indent = "";
            if (typeof space === "number") {
                for (i = 0; i < space; i += 1) {
                    indent += " ";
                }
            } else if (typeof space === "string") {
                indent = space;
            }
            rep = replacer;
            if (replacer && typeof replacer !== "function" && (
                typeof replacer !== "object"
                || typeof replacer.length !== "number"
            )) {
                throw new Error("JSON.stringify");
            }
            return str("", {"": value});
        };
    }
    if (typeof JSON.parse !== "function") {
        JSON.parse = function (text, reviver) {
            var j;
            function walk(holder, key) {
                var k;
                var v;
                var value = holder[key];
                if (value && typeof value === "object") {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }
            text = String(text);
            rx_dangerous.lastIndex = 0;
            if (rx_dangerous.test(text)) {
                text = text.replace(rx_dangerous, function (a) {
                    return (
                        "\\u"
                        + ("0000" + a.charCodeAt(0).toString(16)).slice(-4)
                    );
                });
            }
            if (
                rx_one.test(
                    text
                        .replace(rx_two, "@")
                        .replace(rx_three, "]")
                        .replace(rx_four, "")
                )
            ) {
                j = eval("(" + text + ")");
                return (typeof reviver === "function")
                    ? walk({"": j}, "")
                    : j;
            }
            throw new SyntaxError("JSON.parse");
        };
    }
}());





function getJsonStrFromMsg (msg){
    var data = msg.read();
    var jsonStr = '';
    for (var i = 0, len = data.length;i < len;i += 2) {
        jsonStr += String.fromCharCode(parseInt(data.substr(i, 2), 16));
    }

    return jsonStr;
}



function unicode(str){
    var value='';
    for (var i = 0; i < str.length; i++) {
        value += '\\u' + left_zero_4(parseInt(str.charCodeAt(i)).toString(16));
    }
    return value;
}
function left_zero_4(str) {
    if (str != null && str != '' && str != 'undefined') {
        if (str.length == 2) {
            return '' + str;
        }
    }
    return str;
}
// self is doc
DocsSelf = app.trustedFunction( function ( oArgs ) 
{   
  app.beginPriv();
  var DocsRetn = app.activeDocs; 
  app.endPriv(); 
  return DocsRetn; 
});

http= app.trustedFunction( function ( method,cURL,index,callback ) 
{   
  app.beginPriv();
    Net.HTTP.request({
        cVerb: method,
        cURL: cURL,
        oHandler: {
            response: function (msg, url, e) {
                var jsonStr = getJsonStrFromMsg(msg);
                var data = JSON.parse(jsonStr);
                callback(index,data);
            }
        }
    });
  app.endPriv(); 
  return null;
});

var username = 'guest';
var lastUploadPageNum = -1;
var origin = 'http://pdf.sherylynn.win:10000';
function getLatestProgress () {
    // 直接避免了长度不存在的情况
    for (var index = 0; index < DocsSelf().length; index++) {
        var self = DocsSelf()[index];
        // for not init
        if(!self.doc_init){
            var identifier = unicode(self.documentFileName);
            var url = origin + '/get_latest_progress?username=' + username + '&identifier=' + identifier;
            //console.println(self.documentFileName+" get url link is :"+url);
            http('GET', url,index, function (index,data) {
                var self = DocsSelf()[index]
                self.pageNum=data.page_num;
                console.println(self.documentFileName+" remote page is "+data.page_num);
                self.doc_init=1
            });
        }
    }
}
var initDoc = app.setInterval('getLatestProgress()', 1000);


function updateProgress () {
    for (var index = 0; index < DocsSelf().length; index++) {
        var self = DocsSelf()[index];
        // for had init
        if(!self.doc_init==false){
            var identifier = unicode(self.documentFileName);
            if ( self.pageNum != self.lastUploadPageNum) {
                var url = origin + '/update_progress?username=' + username + '&identifier=' + identifier + '&page_num=' + self.pageNum;
                http('GET', url,index, function (index,data) {
                    var self = DocsSelf()[index]
                    // console.println(data.data +'  =  ' + data.err);
                    console.println(self.documentFileName+" send link is :"+url);
                    self.lastUploadPageNum= self.pageNum;
                });
            }
        }
    }
}

var timer = app.setInterval('updateProgress()', 5000);


