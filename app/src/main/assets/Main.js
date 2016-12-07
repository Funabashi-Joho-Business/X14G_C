function update(){
    var title = document.querySelector("div.title");
    var body = document.querySelector("div.body");
    var tag = document.querySelector("div.tag");
    title.innerHTML = Java.getTitle();
    body.innerHTML = Java.getBody();
    tag,innerHTML = Java.getRankTag();
}