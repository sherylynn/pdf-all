id="henrybolt=\\u201c\\u33\\u30\\u5c81\\u7684\\u6211\\uff0c\\u4e5f\\u66fe\\u662f\\u4ed6\\u4eec\\u53e3\\u4e2d\\u57ae\\u6389\\u7684\\u4e00\\u4ee3\\u201d\\u2e\\u70\\u64\\u66"
id=id.split('=')[1]
char_array=""
id_array=id.split('\\u')
console.log(id_array)
for( var CharCode of id_array){
  true_code=parseInt(CharCode,16)
  //true_code="0x"+CharCode
  console.log(true_code)
  char_array+=String.fromCharCode(true_code)
  console.log(char_array)
}