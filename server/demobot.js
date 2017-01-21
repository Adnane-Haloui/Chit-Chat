
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , user = require('./routes/user')
  , http = require('http')
  , path = require('path');
var config = require('./config.json');
var app = express();

app.configure(function(){
  app.set('port', 4000);
  app.use(express.logger('dev'));
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
});

app.configure('development', function(){
  app.use(express.errorHandler());
});


var text="here is the article. find me here.my name is arun rajora. arun rajora is 18 years old.nitin chand is arun's friend. arun is participating in SRM hackathon alone.";

function tokenize(data,filt)
{
 // console.log(filt);
  var block=[];
  var str="";
  var mode=0;
  var l=0;
  for(var i=0;i<data.length;i++)
  {
    if(data[i]>='a' && data[i]<='z')
    {
      if(mode==3)
      {
        if(str!="" && str!=" " && str!="\n" && str!="\t")
        block.push({'str':str,"start":l,"end":i-1});
        str="";
        l=i;
      }
      mode=1;
       str+=data[i];
    }
    else if(data[i]>='A' && data[i]<='Z')
    {
      if(mode==3)
      {
        if(str!="" && str!=" " && str!="\n" && str!="\t")
        block.push({'str':str,"start":l,"end":i-1});
        str="";
        l=i;
      }
      mode=2;
      data[i]=data[i]-'A'+'a';
    str+=data[i];
    }
    else if(data[i]>='0' && data[i]<='9' )
    {
      if(mode==1 || mode==2)
      {
        if(str!="" && str!=" " && str!="\n" && str!="\t")
        block.push({'str':str,"start":l,"end":i-1});
        str="";
        l=i;
      }
      mode=3;
      str+=data[i];
    }
    else
    {
      mode=0;
      if(str!="" && str!=" " && str!="\n" && str!="\t")
      block.push({'str':str,"start":l,"end":i-1});
      str="";
      l=i+1;
      if(filt==0)
      {
      str+=data[i];
      if(str!="" && str!=" " && str!="\n" && str!="\t")
      block.push({'str':str,"start":l,"end":i-1});
      str="";
   //   l=i+1;
      }
    }

  }
  if(str!="" && str!=" " && str!="\n" && str!="\t")
  block.push({'str':str,"start":l,"end":i-1});
  return block;
}
var inp_text=tokenize(text,0);

//create luis_word
var url=config.luis_secret;
var url_end="&timezoneOffset=5.5";
var request=require('request');

text.split(".").forEach(function(val){
 // console.log(val);
request(url+val+url_end,function(error,response,body){
  if(!error && response.statusCode==200){
        var json=JSON.parse(body);
        json.entities.forEach(function(val){
          for(var xx=0;xx<inp_text.length;xx++){
           // console.log(val.entity);
            //onsole.log(inp_text[xx].str);
            //console.log(val.entity.toLowerCase().split(' '));
            if(-1 != (val.entity.toLowerCase().split(' ')).indexOf(inp_text[xx].str.toLowerCase())){//inp_text[xx].start>=val.startIndex && inp_text[xx].end<=val.endIndex){
              if(!inp_text[xx].hasOwnProperty('luis')) inp_text[xx].luis=[];
              val.type.split('.').forEach(function(val){
                if(inp_text[xx].luis.indexOf(val)==-1)
                  inp_text[xx].luis.push(val);
              });
              //inp_text[xx].luis.push(val.type.split('.'));
            }
          }
//          console.log(inp_text);
        });
  }
});
});
request(url+text+url_end,function(error,response,body){
  if(!error && response.statusCode==200){
        var json=JSON.parse(body);
        json.entities.forEach(function(val){
          for(var xx=0;xx<inp_text.length;xx++){
           // console.log(val.entity);
            //onsole.log(inp_text[xx].str);
            //console.log(val.entity.toLowerCase().split(' '));
            if(-1 != (val.entity.toLowerCase().split(' ')).indexOf(inp_text[xx].str.toLowerCase())){//inp_text[xx].start>=val.startIndex && inp_text[xx].end<=val.endIndex){
              if(!inp_text[xx].hasOwnProperty('luis')) inp_text[xx].luis=[];
              val.type.split('.').forEach(function(val){
                if(inp_text[xx].luis.indexOf(val)==-1)
                  inp_text[xx].luis.push(val);
              });
              //inp_text[xx].luis.push(val.type.split('.'));
            }
          }
 //         console.log(inp_text);
        });
  }
});



//console.log(inp_text);







function filterData(heha){
  var ans=[];
  var black="and what who where how be in is a an it is on of me am the where";
  for(var i=0;i<heha.length;i++)
  {
    if(black.search(heha[i].str)==-1)
    ans.push(heha[i]);
  }
  return ans;
}

function Isposs(inp,out,pp)
{
  var ilen=inp.length;
  var olen=out.length;
  var dp=[];
  for(var i=0;i<=olen;i++)
  {
    dp.push([]);
    for(var j=0;j<=ilen;j++)
    {
      if(i==0 || j==0)
        dp[i].push(0);
      else if(out[i-1].str.search(inp[j-1].str)!=-1 || (out[i-1].luis!=undefined && out[i-1].luis.indexOf(inp[j-1].str)!=-1))
        dp[i].push(1);
      else
      dp[i].push(0);
    }
  }
  for(var i=1;i<=olen;i++)
  {
    for(var j=1;j<=ilen;j++)
    {
      dp[i][j]+=dp[i-1][j];
    }
  }
  var minLen=-1;
  var lo=0,hi=0;
  for(var i=1;i<=olen;i++)
  {
    var dist=-1;
    hi=i;
    for(;lo<hi;lo++)
    {
      var ke=0;
      for(var j=1;j<=ilen;j++)
      {
        if((dp[hi][j]-dp[lo][j])>0)
        {
          ke++;
        }
      }
      if(ke>=pp)
        dist=1;
      else
        break;
    }
    if(dist!=-1)
    {
      lo--;
      if(minLen==-1)
      {
        minLen=hi-lo;
      }
      else if(minLen>(hi-lo))
      {
        minLen=hi-lo;
      }
    }
  }

  return minLen;  
}

function mergeTokens(arr)
{
  var ans="";
  for(var i=0;i<arr.length;i++)
  {
    ans+=arr[i].str+" ";
  }
  return ans;
}

function CalcValueBot(inp,out,mcoun,msiz)
{
  var ans={x:[],y:[]};
  var ilen=inp.length;
  var olen=out.length;
  var dp=[];
  for(var i=0;i<=olen;i++)
  {
    dp.push([]);
    for(var j=0;j<=ilen;j++)
    {
      if(i==0 || j==0)
        dp[i].push(0);
      else if(out[i-1].str.search(inp[j-1].str)!=-1 || (out[i-1].luis!=undefined && out[i-1].luis.indexOf(inp[j-1].str)!=-1))
        dp[i].push(1);
      else
      dp[i].push(0);
    }
  }
  for(var i=1;i<=olen;i++)
  {
    for(var j=1;j<=ilen;j++)
    {
      dp[i][j]+=dp[i-1][j];
    }
  }
  var minLen=-1;
  var lo=0,hi=0;
  for(var i=0;i<=olen;i++)
  {
    var lo=i,hi=i+msiz;
    if(hi>olen)
      break;
    var ke=0;
      for(var j=1;j<=ilen;j++)
      {
        if((dp[hi][j]-dp[lo][j])>0)
        {
          ke++;
        }
      }
      if(ke>=mcoun)
      {
        var aa=lo;
        var bb=hi-1;
        while(aa>=0)
        {
          if(out[aa].str=="." || out[aa].str=="?" || out[aa].str=="\n" || out[aa].str=="!")
          {
            aa++;
            break;
          }
          aa--;
        }
        while(bb<=olen)
        {
          if(out[bb].str=="." || out[bb].str=="?" || out[bb].str=="\n" || out[bb].str=="!")
          {
            break;
          }
          bb++;
        }
        ans.x.push(aa);
        ans.y.push(bb);
      }
  }
var final={x:[],y:[]};
var curre=0;
for(var i=0;i<ans.x.length;i++)
{
  if(i==0)
  {
    final.x.push(ans.x[0]);
    final.y.push(ans.y[0]);
  }
  else
  {
    if(final.y[curre]+1>=ans.x[i])
      final.y[curre]=ans.y[i];
    else
    {
      curre++;
      final.x.push(ans.x[i]);
      final.y.push(ans.y[i]);
    }
  }
}

//console.log(final);
var outt=[];
var currentt=0;
var isopen=0;
for(var i=0;i<olen;i++)
{
  if(final.x[currentt]==-1)
    final.x[currentt]=0;
  if(final.x[currentt]==i)
  {
    isopen=1;
  }
  if(isopen==1)
  outt.push(out[i]);
  if(final.y[currentt]==i)
  {
    isopen=0;
    currentt++;
  }
}
if(outt.length==0)
{
  outt.push({"str":"No records found on that!!!"});
}
  return outt;
}


app.post("/bot",function(req,res)
{
console.log(req.body);

  if(req.body.messageText=="")
  {
    res.send(":)");
  }
  else if(req.body.messageText=="hi" || req.body.messageText=="hello" || req.body.messageText=="hey")
  {
    res.send("Hola, how may i help you?");
  }
  else{
    var question=req.body.messageText;
    question=question.toLowerCase();
    question=question.replace("do you think","");
    var quesTokens=tokenize(question,1);
    quesTokens=filterData(quesTokens);
    if(quesTokens.length==0)
    {
      res.send("Sorry,No bingos this time :(");
    }
    var lenInp=quesTokens.length;
    var fou=-1,heh=-1;
    for(var i=lenInp;i>0;i--)
    {
      var gott=Isposs(quesTokens,inp_text,i);
   //   console.log("got "+i+" "+gott);
      if(gott!=-1)
      {
        heh=gott;
        fou=i;
        break;
      }
    }
    if(fou==-1)
    {
      res.send("Sorry,Bingo got lost in the text :(");
    }
    if(fou!=-1)
    output=CalcValueBot(quesTokens,inp_text,fou,gott);
  console.log("here-------------------------->");
 //   console.log(output);
    res.send(mergeTokens(output));

  if(-1!=question.indexOf("what")){

  }
  else if(-1!=question.indexOf("who")){
    
  }
  else if(-1!=question.indexOf("when")){
    
  }
  else if(-1!=question.indexOf("where")){
    
  }
  else if(-1!=question.indexOf("how")){
    
  }
  else if(question.length>3 && question[0]=='i' && question[1]=='s' && question[2]==' '){
    
  }
  else{

  }
 // console.log(quesTokens);
 // res.send(question);
}});

http.createServer(app).listen(app.get('port'), function(){
  console.log("Express server listening on port " + app.get('port'));
});
