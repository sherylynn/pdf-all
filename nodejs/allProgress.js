var fs=require('fs')
var jsonPath = __dirname+"/../django/homepage/progress.json"
var progress=require(jsonPath)
var progress_cn={}
// not standard unescape method

/*
for(var id in progress){
  id=id.split('=')[1]
  id_URI=id.replace(/\\u/g, '%u')
  console.log(id_URI)
  console.log(unescape(id_URI))
  progress_cn[id]=progress[id]
}
*/
//console.log(progress_cn)



for(var id in progress){  // progress is obj ; so for in
  // split henrybolt=
  old_id=id
  id=id.split('=')[1]
  // split \\u
  id_array=id.split('\\u')
  char_array=""
  for( var CharCode of id_array){ // id_array is array ; so for of
    if(CharCode == ""){
      // jump out of space ""
      continue
    }
    // change to num 16
    true_code=parseInt(CharCode,16)

    // change to char
    char_array+=String.fromCharCode(true_code)
  }
  //console.log(char_array)
  progress_cn[char_array]=progress[old_id]
}
//console.log(progress_cn)

var progress_cn_stringify=JSON.stringify(progress_cn,null,'\t')

fs.writeFile(__dirname+'/progress_cn.json',progress_cn_stringify, 'utf8', (err) => {
  if (err) throw err;
  console.log('done');
});
/*
console.log(String.fromCharCode(123,45));
console.log(String.fromCharCode([123,45]));
*/