var page   = require('webpage').create(),
    system = require('system');

page.paperSize = {
  format: "A4",
  orientation: "portrait",
  margin: {left:"2.5cm", right:"2.5cm", top:"1cm", bottom:"1cm"},
  footer: {
    height: "0.9cm",
    contents: phantom.callback(function(pageNum, numPages) {
      return "<div style='text-align:center;'><small>" + pageNum +
        " / " + numPages + "</small></div>";
    })}};

function randomString(length, chars) {
  var result = '';
  for (var i = length; i > 0; --i)
    result += chars[Math.round(Math.random() * (chars.length - 1))];
  return result;
}

var captured = false;
function capture() {
  var url = system.stdin.readLine();
  if (!url)
    phantom.exit(1);
  page.open(url, function(status) {
    var captureName ='ss_' + randomString(8, 'abcdefghijklmnopqrstuvwxyz0123456789') + '.pdf';
    page.render(captureName);
    system.stdout.writeLine(captureName);
    system.stdout.flush();
    captured = true;
  });
  waitFor();
}

function waitFor() {
  var interval = setInterval(function() {
    if(captured) {
      clearInterval(interval);
      captured = false;
      capture();
    }
  }, 250);
}

capture();