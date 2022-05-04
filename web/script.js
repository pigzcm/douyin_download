String.prototype.english = (function(){
    let rtn = '';
    for (let i = 0; i < this.length; i ++) {
        let code = this[i].charCodeAt(0);
        if(code >= 33 && code <= 126){
            rtn += this[i];
        }
    }
    return rtn;
});
String.prototype.toUrl = (function(){
    return encodeURIComponent(this);
});
String.prototype.douyin = (function(){
    let english = this.english();
    console.log(english)
    let https = english.indexOf('https://');
    if (https == -1){
        return '';
    }
    let end = english.lastIndexOf('/');
    if (end <= https){
        end = english.length;
    }
    console.log(end)
    return english.substring(https,end);
});
window.user_agent = ("Mozilla/5.0 (Linux; Android 11; PECM30)"+
  "AppleWebKit/537.36 (KHTML, like Gecko) " +
  "Chrome/99.0.4844.73 Mobile Safari/537.36");