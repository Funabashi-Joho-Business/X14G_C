function setText(tag,msg){
    var node = document.querySelector(tag);
    node.innerHTML = msg;
}
function addText(tag,msg){
    var node = document.querySelector(tag);
    node.innerHTML += msg;
}
function setStyle(tag,name,value){
    var node = document.querySelector(tag);
    node.style[name] = value;
}