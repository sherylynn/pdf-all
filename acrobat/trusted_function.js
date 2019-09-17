/*
DocsSelf = app.trustedFunction( function ( oArgs ) 
{   
  app.beginPriv();
  var DocsRetn = app.activeDocs; 
  app.endPriv(); 
  return DocsRetn; 
});

var docZeroName = function(docs){
  return docs[0].documentFileName
}
var printDocName = function(){
  console.println(docZeroName(DocsSelf()))
}
var timer = app.setInterval('printDocName()', 1000);
*/
/*
DocsSelf = app.trustedFunction( function ( oArgs ) 
{   
  app.beginPriv();
  var DocsRetn = app.activeDocs[0].documentFileName; 
  app.endPriv(); 
  return DocsRetn; 
});

var printDocName = function(){
  console.println(DocsSelf())
}
var timer = app.setInterval('printDocName()', 1000)
*/
DocsSelf = app.trustedFunction( function ( oArgs ) 
{   
  app.beginPriv();
  var DocsRetn = app.activeDocs; 
  app.endPriv(); 
  return DocsRetn; 
});

var printDocName = function(){
  console.println(DocsSelf()[0].documentFileName)
}
var timer = app.setInterval('printDocName()', 1000)