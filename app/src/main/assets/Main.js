function update(){
    var body = document.querySelector("div.body");
    body.innerHTML = Java.getBody();
}
function setText(tag,msg){
    var node = document.querySelector(tag);
    node.innerHTML = msg;
}
function setStyle(tag,name,value){
    var node = document.querySelector(tag);
    node.style[name] = value;
}