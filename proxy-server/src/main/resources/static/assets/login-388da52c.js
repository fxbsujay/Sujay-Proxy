import{d as m,r as f,a as g,b as p,o as v,c as h,e as s,f as l,w as x,g as w,p as b,h as S,i as u}from"./index-620e4e6b.js";const _=o=>(b("data-v-278c413e"),o=o(),S(),o),y={class:"login-bg"},C=_(()=>s("span",{class:"title"},"PROXY",-1)),k={class:"form-box"},B=_(()=>s("span",{class:"footer"},[u("Simple-Proxy ©2023 Created by "),s("a",{href:"http://xuebin.xyz"},"Fan XueBin")],-1)),I=m({__name:"login",setup(o){const t=f(!1),e=g({username:"",password:""}),n=()=>{!e.username||e.username.trim().length<=4||!e.password||e.password.trim().length<=4||(t.value=!0,w().login(e).catch(()=>t.value=!1))};return(c,a)=>{const i=p("a-input"),d=p("a-button");return v(),h("div",y,[C,s("form",k,[l(i,{value:e.username,"onUpdate:value":a[0]||(a[0]=r=>e.username=r),size:"large",placeholder:"Username"},null,8,["value"]),l(i,{value:e.password,"onUpdate:value":a[1]||(a[1]=r=>e.password=r),size:"large",placeholder:"Password"},null,8,["value"]),l(d,{loading:t.value,onClick:n},{default:x(()=>[u(" Login ")]),_:1},8,["loading"])]),B])}}});const z=(o,t)=>{const e=o.__vccOpts||o;for(const[n,c]of t)e[n]=c;return e},P=z(I,[["__scopeId","data-v-278c413e"]]);export{P as default};