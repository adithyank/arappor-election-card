window.sirpi = window.sirpi || {}

sirpi.callFn = function(functionName, context, args) {
  var args = Array.prototype.slice.call(arguments, 2);
  var namespaces = functionName.split(".");
  var func = namespaces.pop();
  for(var i = 0; i < namespaces.length; i++) {
    context = context[namespaces[i]];
  }
  return context[func].apply(context, args);
}

sirpi.str = sirpi.str || {}

sirpi.str.hasLength = function(str) {
    return str != null && str != undefined && str.length > 0
}

sirpi.loadDiv = function(id, url)
{
    $.ajax({
        mimeType: 'text/html; charset=utf-8',
        url: url,
        type: 'GET',
        success: function(data) {
           $('#'+id).html(data);
        },
        dataType: "html",
        async: false
    });
}

sirpi.rest = function(data)
{
    $.ajax(data)
}

sirpi.toast = sirpi.toast || {}

sirpi.toast.success = function(msg, header, options)
{
    tata.success(msg, header, {duration: 4000})
}

sirpi.toast.info = function(msg, header, options)
{
    tata.info(msg, header, {duration: 4000})
}

sirpi.toast.error = function(msg, header, options)
{
    tata.error(msg, header, {duration: 4000})
}

sirpi.sleep = function(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
