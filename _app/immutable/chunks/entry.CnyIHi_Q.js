import{x as B,ar as Ve,d as dt,as as ht,a7 as pt}from"./utils.C_JF9SDz.js";new URL("sveltekit-internal://");function gt(e,n){return e==="/"||n==="ignore"?e:n==="never"?e.endsWith("/")?e.slice(0,-1):e:n==="always"&&!e.endsWith("/")?e+"/":e}function mt(e){return e.split("%25").map(decodeURI).join("%25")}function yt(e){for(const n in e)e[n]=decodeURIComponent(e[n]);return e}function ue({href:e}){return e.split("#")[0]}const _t=["href","pathname","search","toString","toJSON"];function wt(e,n,t){const r=new URL(e);Object.defineProperty(r,"searchParams",{value:new Proxy(r.searchParams,{get(a,o){if(o==="get"||o==="getAll"||o==="has")return s=>(t(s),a[o](s));n();const i=Reflect.get(a,o);return typeof i=="function"?i.bind(a):i}}),enumerable:!0,configurable:!0});for(const a of _t)Object.defineProperty(r,a,{get(){return n(),e[a]},enumerable:!0,configurable:!0});return r}const bt="/__data.json",vt=".html__data.json";function At(e){return e.endsWith(".html")?e.replace(/\.html$/,vt):e.replace(/\/$/,"")+bt}function kt(...e){let n=5381;for(const t of e)if(typeof t=="string"){let r=t.length;for(;r;)n=n*33^t.charCodeAt(--r)}else if(ArrayBuffer.isView(t)){const r=new Uint8Array(t.buffer,t.byteOffset,t.byteLength);let a=r.length;for(;a;)n=n*33^r[--a]}else throw new TypeError("value must be a string or TypedArray");return(n>>>0).toString(36)}function Et(e){const n=atob(e),t=new Uint8Array(n.length);for(let r=0;r<n.length;r++)t[r]=n.charCodeAt(r);return t.buffer}const qe=window.fetch;window.fetch=(e,n)=>((e instanceof Request?e.method:(n==null?void 0:n.method)||"GET")!=="GET"&&G.delete(ye(e)),qe(e,n));const G=new Map;function St(e,n){const t=ye(e,n),r=document.querySelector(t);if(r!=null&&r.textContent){let{body:a,...o}=JSON.parse(r.textContent);const i=r.getAttribute("data-ttl");return i&&G.set(t,{body:a,init:o,ttl:1e3*Number(i)}),r.getAttribute("data-b64")!==null&&(a=Et(a)),Promise.resolve(new Response(a,o))}return window.fetch(e,n)}function Rt(e,n,t){if(G.size>0){const r=ye(e,t),a=G.get(r);if(a){if(performance.now()<a.ttl&&["default","force-cache","only-if-cached",void 0].includes(t==null?void 0:t.cache))return new Response(a.body,a.init);G.delete(r)}}return window.fetch(n,t)}function ye(e,n){let r=`script[data-sveltekit-fetched][data-url=${JSON.stringify(e instanceof Request?e.url:e)}]`;if(n!=null&&n.headers||n!=null&&n.body){const a=[];n.headers&&a.push([...new Headers(n.headers)].join(",")),n.body&&(typeof n.body=="string"||ArrayBuffer.isView(n.body))&&a.push(n.body),r+=`[data-hash="${kt(...a)}"]`}return r}const It=/^(\[)?(\.\.\.)?(\w+)(?:=(\w+))?(\])?$/;function Tt(e){const n=[];return{pattern:e==="/"?/^\/$/:new RegExp(`^${Lt(e).map(r=>{const a=/^\[\.\.\.(\w+)(?:=(\w+))?\]$/.exec(r);if(a)return n.push({name:a[1],matcher:a[2],optional:!1,rest:!0,chained:!0}),"(?:/(.*))?";const o=/^\[\[(\w+)(?:=(\w+))?\]\]$/.exec(r);if(o)return n.push({name:o[1],matcher:o[2],optional:!0,rest:!1,chained:!0}),"(?:/([^/]+))?";if(!r)return;const i=r.split(/\[(.+?)\](?!\])/);return"/"+i.map((c,l)=>{if(l%2){if(c.startsWith("x+"))return de(String.fromCharCode(parseInt(c.slice(2),16)));if(c.startsWith("u+"))return de(String.fromCharCode(...c.slice(2).split("-").map(f=>parseInt(f,16))));const d=It.exec(c),[,h,g,u,p]=d;return n.push({name:u,matcher:p,optional:!!h,rest:!!g,chained:g?l===1&&i[0]==="":!1}),g?"(.*?)":h?"([^/]*)?":"([^/]+?)"}return de(c)}).join("")}).join("")}/?$`),params:n}}function Ut(e){return!/^\([^)]+\)$/.test(e)}function Lt(e){return e.slice(1).split("/").filter(Ut)}function xt(e,n,t){const r={},a=e.slice(1),o=a.filter(s=>s!==void 0);let i=0;for(let s=0;s<n.length;s+=1){const c=n[s];let l=a[s-i];if(c.chained&&c.rest&&i&&(l=a.slice(s-i,s+1).filter(d=>d).join("/"),i=0),l===void 0){c.rest&&(r[c.name]="");continue}if(!c.matcher||t[c.matcher](l)){r[c.name]=l;const d=n[s+1],h=a[s+1];d&&!d.rest&&d.optional&&h&&c.chained&&(i=0),!d&&!h&&Object.keys(r).length===o.length&&(i=0);continue}if(c.optional&&c.chained){i++;continue}return}if(!i)return r}function de(e){return e.normalize().replace(/[[\]]/g,"\\$&").replace(/%/g,"%25").replace(/\//g,"%2[Ff]").replace(/\?/g,"%3[Ff]").replace(/#/g,"%23").replace(/[.*+?^${}()|\\]/g,"\\$&")}function Pt({nodes:e,server_loads:n,dictionary:t,matchers:r}){const a=new Set(n);return Object.entries(t).map(([s,[c,l,d]])=>{const{pattern:h,params:g}=Tt(s),u={id:s,exec:p=>{const f=h.exec(p);if(f)return xt(f,g,r)},errors:[1,...d||[]].map(p=>e[p]),layouts:[0,...l||[]].map(i),leaf:o(c)};return u.errors.length=u.layouts.length=Math.max(u.errors.length,u.layouts.length),u});function o(s){const c=s<0;return c&&(s=~s),[c,e[s]]}function i(s){return s===void 0?s:[a.has(s),e[s]]}}function Be(e,n=JSON.parse){try{return n(sessionStorage[e])}catch{}}function Ue(e,n,t=JSON.stringify){const r=t(n);try{sessionStorage[e]=r}catch{}}const N=[];function Ct(e,n){return{subscribe:ae(e,n).subscribe}}function ae(e,n=B){let t=null;const r=new Set;function a(s){if(ht(e,s)&&(e=s,t)){const c=!N.length;for(const l of r)l[1](),N.push(l,e);if(c){for(let l=0;l<N.length;l+=2)N[l][0](N[l+1]);N.length=0}}}function o(s){a(s(e))}function i(s,c=B){const l=[s,c];return r.add(l),r.size===1&&(t=n(a,o)||B),s(e),()=>{r.delete(l),r.size===0&&t&&(t(),t=null)}}return{set:a,update:o,subscribe:i}}function un(e,n,t){const r=!Array.isArray(e),a=r?[e]:e;if(!a.every(Boolean))throw new Error("derived() expects stores as input, got a falsy value");const o=n.length<2;return Ct(t,(i,s)=>{let c=!1;const l=[];let d=0,h=B;const g=()=>{if(d)return;h();const p=n(r?l[0]:l,i,s);o?i(p):h=typeof p=="function"?p:B},u=a.map((p,f)=>Ve(p,m=>{l[f]=m,d&=~(1<<f),c&&g()},()=>{d|=1<<f}));return c=!0,g(),function(){dt(u),h(),c=!1}})}function dn(e){let n;return Ve(e,t=>n=t)(),n}var De;const U=((De=globalThis.__sveltekit_1hq845s)==null?void 0:De.base)??"";var Fe;const Nt=((Fe=globalThis.__sveltekit_1hq845s)==null?void 0:Fe.assets)??U,Ot="1731177302980",Ge="sveltekit:snapshot",Me="sveltekit:scroll",He="sveltekit:states",jt="sveltekit:pageurl",$="sveltekit:history",H="sveltekit:navigation",J={tap:1,hover:2,viewport:3,eager:4,off:-1,false:-1},Y=location.origin;function Ke(e){if(e instanceof URL)return e;let n=document.baseURI;if(!n){const t=document.getElementsByTagName("base");n=t.length?t[0].href:document.URL}return new URL(e,n)}function _e(){return{x:pageXOffset,y:pageYOffset}}function O(e,n){return e.getAttribute(`data-sveltekit-${n}`)}const Le={...J,"":J.hover};function We(e){let n=e.assignedSlot??e.parentNode;return(n==null?void 0:n.nodeType)===11&&(n=n.host),n}function Ye(e,n){for(;e&&e!==n;){if(e.nodeName.toUpperCase()==="A"&&e.hasAttribute("href"))return e;e=We(e)}}function pe(e,n){let t;try{t=new URL(e instanceof SVGAElement?e.href.baseVal:e.href,document.baseURI)}catch{}const r=e instanceof SVGAElement?e.target.baseVal:e.target,a=!t||!!r||oe(t,n)||(e.getAttribute("rel")||"").split(/\s+/).includes("external"),o=(t==null?void 0:t.origin)===Y&&e.hasAttribute("download");return{url:t,external:a,target:r,download:o}}function X(e){let n=null,t=null,r=null,a=null,o=null,i=null,s=e;for(;s&&s!==document.documentElement;)r===null&&(r=O(s,"preload-code")),a===null&&(a=O(s,"preload-data")),n===null&&(n=O(s,"keepfocus")),t===null&&(t=O(s,"noscroll")),o===null&&(o=O(s,"reload")),i===null&&(i=O(s,"replacestate")),s=We(s);function c(l){switch(l){case"":case"true":return!0;case"off":case"false":return!1;default:return}}return{preload_code:Le[r??"off"],preload_data:Le[a??"off"],keepfocus:c(n),noscroll:c(t),reload:c(o),replace_state:c(i)}}function xe(e){const n=ae(e);let t=!0;function r(){t=!0,n.update(i=>i)}function a(i){t=!1,n.set(i)}function o(i){let s;return n.subscribe(c=>{(s===void 0||t&&c!==s)&&i(s=c)})}return{notify:r,set:a,subscribe:o}}function $t(){const{set:e,subscribe:n}=ae(!1);let t;async function r(){clearTimeout(t);try{const a=await fetch(`${Nt}/_app/version.json`,{headers:{pragma:"no-cache","cache-control":"no-cache"}});if(!a.ok)return!1;const i=(await a.json()).version!==Ot;return i&&(e(!0),clearTimeout(t)),i}catch{return!1}}return{subscribe:n,check:r}}function oe(e,n){return e.origin!==Y||!e.pathname.startsWith(n)}function Pe(e){const n=Ft(e),t=new ArrayBuffer(n.length),r=new DataView(t);for(let a=0;a<t.byteLength;a++)r.setUint8(a,n.charCodeAt(a));return t}const Dt="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";function Ft(e){e.length%4===0&&(e=e.replace(/==?$/,""));let n="",t=0,r=0;for(let a=0;a<e.length;a++)t<<=6,t|=Dt.indexOf(e[a]),r+=6,r===24&&(n+=String.fromCharCode((t&16711680)>>16),n+=String.fromCharCode((t&65280)>>8),n+=String.fromCharCode(t&255),t=r=0);return r===12?(t>>=4,n+=String.fromCharCode(t)):r===18&&(t>>=2,n+=String.fromCharCode((t&65280)>>8),n+=String.fromCharCode(t&255)),n}const Vt=-1,qt=-2,Bt=-3,Gt=-4,Mt=-5,Ht=-6;function Kt(e,n){if(typeof e=="number")return a(e,!0);if(!Array.isArray(e)||e.length===0)throw new Error("Invalid input");const t=e,r=Array(t.length);function a(o,i=!1){if(o===Vt)return;if(o===Bt)return NaN;if(o===Gt)return 1/0;if(o===Mt)return-1/0;if(o===Ht)return-0;if(i)throw new Error("Invalid input");if(o in r)return r[o];const s=t[o];if(!s||typeof s!="object")r[o]=s;else if(Array.isArray(s))if(typeof s[0]=="string"){const c=s[0],l=n==null?void 0:n[c];if(l)return r[o]=l(a(s[1]));switch(c){case"Date":r[o]=new Date(s[1]);break;case"Set":const d=new Set;r[o]=d;for(let u=1;u<s.length;u+=1)d.add(a(s[u]));break;case"Map":const h=new Map;r[o]=h;for(let u=1;u<s.length;u+=2)h.set(a(s[u]),a(s[u+1]));break;case"RegExp":r[o]=new RegExp(s[1],s[2]);break;case"Object":r[o]=Object(s[1]);break;case"BigInt":r[o]=BigInt(s[1]);break;case"null":const g=Object.create(null);r[o]=g;for(let u=1;u<s.length;u+=2)g[s[u]]=a(s[u+1]);break;case"Int8Array":case"Uint8Array":case"Uint8ClampedArray":case"Int16Array":case"Uint16Array":case"Int32Array":case"Uint32Array":case"Float32Array":case"Float64Array":case"BigInt64Array":case"BigUint64Array":{const u=globalThis[c],p=s[1],f=Pe(p),m=new u(f);r[o]=m;break}case"ArrayBuffer":{const u=s[1],p=Pe(u);r[o]=p;break}default:throw new Error(`Unknown type ${c}`)}}else{const c=new Array(s.length);r[o]=c;for(let l=0;l<s.length;l+=1){const d=s[l];d!==qt&&(c[l]=a(d))}}else{const c={};r[o]=c;for(const l in s){const d=s[l];c[l]=a(d)}}return r[o]}return a(0)}const ze=new Set(["load","prerender","csr","ssr","trailingSlash","config"]);[...ze];const Wt=new Set([...ze]);[...Wt];function Yt(e){return e.filter(n=>n!=null)}class se{constructor(n,t){this.status=n,typeof t=="string"?this.body={message:t}:t?this.body=t:this.body={message:`Error: ${n}`}}toString(){return JSON.stringify(this.body)}}class Je{constructor(n,t){this.status=n,this.location=t}}class we extends Error{constructor(n,t,r){super(r),this.status=n,this.text=t}}const zt="x-sveltekit-invalidated",Jt="x-sveltekit-trailing-slash";function Z(e){return e instanceof se||e instanceof we?e.status:500}function Xt(e){return e instanceof we?e.text:"Internal Error"}const C=Be(Me)??{},K=Be(Ge)??{},x={url:xe({}),page:xe({}),navigating:ae(null),updated:$t()};function be(e){C[e]=_e()}function Zt(e,n){let t=e+1;for(;C[t];)delete C[t],t+=1;for(t=n+1;K[t];)delete K[t],t+=1}function F(e){return location.href=e.href,new Promise(()=>{})}async function Xe(){if("serviceWorker"in navigator){const e=await navigator.serviceWorker.getRegistration(U||"/");e&&await e.update()}}function Ce(){}let ie,ge,Q,L,me,V;const Ze=[],ee=[];let R=null;const Qe=[],Qt=[];let j=[],_={branch:[],error:null,url:null},ve=!1,te=!1,Ne=!0,W=!1,q=!1,et=!1,Ae=!1,ke,E,T,I,ne;const M=new Set;async function hn(e,n,t){var a,o;document.URL!==location.href&&(location.href=location.href),V=e,ie=Pt(e),L=document.documentElement,me=n,ge=e.nodes[0],Q=e.nodes[1],ge(),Q(),E=(a=history.state)==null?void 0:a[$],T=(o=history.state)==null?void 0:o[H],E||(E=T=Date.now(),history.replaceState({...history.state,[$]:E,[H]:T},""));const r=C[E];r&&(history.scrollRestoration="manual",scrollTo(r.x,r.y)),t?await cn(me,t):on(location.href,{replaceState:!0}),sn()}function en(){Ze.length=0,Ae=!1}function tt(e){ee.some(n=>n==null?void 0:n.snapshot)&&(K[e]=ee.map(n=>{var t;return(t=n==null?void 0:n.snapshot)==null?void 0:t.capture()}))}function nt(e){var n;(n=K[e])==null||n.forEach((t,r)=>{var a,o;(o=(a=ee[r])==null?void 0:a.snapshot)==null||o.restore(t)})}function Oe(){be(E),Ue(Me,C),tt(T),Ue(Ge,K)}async function rt(e,n,t,r){return z({type:"goto",url:Ke(e),keepfocus:n.keepFocus,noscroll:n.noScroll,replace_state:n.replaceState,state:n.state,redirect_count:t,nav_token:r,accept:()=>{n.invalidateAll&&(Ae=!0)}})}async function tn(e){if(e.id!==(R==null?void 0:R.id)){const n={};M.add(n),R={id:e.id,token:n,promise:ot({...e,preload:n}).then(t=>(M.delete(n),t.type==="loaded"&&t.state.error&&(R=null),t))}}return R.promise}async function he(e){const n=ie.find(t=>t.exec(st(e)));n&&await Promise.all([...n.layouts,n.leaf].map(t=>t==null?void 0:t[1]()))}function at(e,n,t){var o;_=e.state;const r=document.querySelector("style[data-sveltekit]");r&&r.remove(),I=e.props.page,ke=new V.root({target:n,props:{...e.props,stores:x,components:ee},hydrate:t,sync:!1}),nt(T);const a={from:null,to:{params:_.params,route:{id:((o=_.route)==null?void 0:o.id)??null},url:new URL(location.href)},willUnload:!1,type:"enter",complete:Promise.resolve()};j.forEach(i=>i(a)),te=!0}function re({url:e,params:n,branch:t,status:r,error:a,route:o,form:i}){let s="never";if(U&&(e.pathname===U||e.pathname===U+"/"))s="always";else for(const u of t)(u==null?void 0:u.slash)!==void 0&&(s=u.slash);e.pathname=gt(e.pathname,s),e.search=e.search;const c={type:"loaded",state:{url:e,params:n,branch:t,error:a,route:o},props:{constructors:Yt(t).map(u=>u.node.component),page:I}};i!==void 0&&(c.props.form=i);let l={},d=!I,h=0;for(let u=0;u<Math.max(t.length,_.branch.length);u+=1){const p=t[u],f=_.branch[u];(p==null?void 0:p.data)!==(f==null?void 0:f.data)&&(d=!0),p&&(l={...l,...p.data},d&&(c.props[`data_${h}`]=l),h+=1)}return(!_.url||e.href!==_.url.href||_.error!==a||i!==void 0&&i!==I.form||d)&&(c.props.page={error:a,params:n,route:{id:(o==null?void 0:o.id)??null},state:{},status:r,url:new URL(e),form:i??null,data:d?l:I.data}),c}async function Ee({loader:e,parent:n,url:t,params:r,route:a,server_data_node:o}){var d,h,g;let i=null,s=!0;const c={dependencies:new Set,params:new Set,parent:!1,route:!1,url:!1,search_params:new Set},l=await e();if((d=l.universal)!=null&&d.load){let u=function(...f){for(const m of f){const{href:v}=new URL(m,t);c.dependencies.add(v)}};const p={route:new Proxy(a,{get:(f,m)=>(s&&(c.route=!0),f[m])}),params:new Proxy(r,{get:(f,m)=>(s&&c.params.add(m),f[m])}),data:(o==null?void 0:o.data)??null,url:wt(t,()=>{s&&(c.url=!0)},f=>{s&&c.search_params.add(f)}),async fetch(f,m){let v;f instanceof Request?(v=f.url,m={body:f.method==="GET"||f.method==="HEAD"?void 0:await f.blob(),cache:f.cache,credentials:f.credentials,headers:f.headers,integrity:f.integrity,keepalive:f.keepalive,method:f.method,mode:f.mode,redirect:f.redirect,referrer:f.referrer,referrerPolicy:f.referrerPolicy,signal:f.signal,...m}):v=f;const S=new URL(v,t);return s&&u(S.href),S.origin===t.origin&&(v=S.href.slice(t.origin.length)),te?Rt(v,S.href,m):St(v,m)},setHeaders:()=>{},depends:u,parent(){return s&&(c.parent=!0),n()},untrack(f){s=!1;try{return f()}finally{s=!0}}};i=await l.universal.load.call(null,p)??null}return{node:l,loader:e,server:o,universal:(h=l.universal)!=null&&h.load?{type:"data",data:i,uses:c}:null,data:i??(o==null?void 0:o.data)??null,slash:((g=l.universal)==null?void 0:g.trailingSlash)??(o==null?void 0:o.slash)}}function je(e,n,t,r,a,o){if(Ae)return!0;if(!a)return!1;if(a.parent&&e||a.route&&n||a.url&&t)return!0;for(const i of a.search_params)if(r.has(i))return!0;for(const i of a.params)if(o[i]!==_.params[i])return!0;for(const i of a.dependencies)if(Ze.some(s=>s(new URL(i))))return!0;return!1}function Se(e,n){return(e==null?void 0:e.type)==="data"?e:(e==null?void 0:e.type)==="skip"?n??null:null}function nn(e,n){if(!e)return new Set(n.searchParams.keys());const t=new Set([...e.searchParams.keys(),...n.searchParams.keys()]);for(const r of t){const a=e.searchParams.getAll(r),o=n.searchParams.getAll(r);a.every(i=>o.includes(i))&&o.every(i=>a.includes(i))&&t.delete(r)}return t}function $e({error:e,url:n,route:t,params:r}){return{type:"loaded",state:{error:e,url:n,route:t,params:r,branch:[]},props:{page:I,constructors:[]}}}async function ot({id:e,invalidating:n,url:t,params:r,route:a,preload:o}){if((R==null?void 0:R.id)===e)return M.delete(R.token),R.promise;const{errors:i,layouts:s,leaf:c}=a,l=[...s,c];i.forEach(y=>y==null?void 0:y().catch(()=>{})),l.forEach(y=>y==null?void 0:y[1]().catch(()=>{}));let d=null;const h=_.url?e!==_.url.pathname+_.url.search:!1,g=_.route?a.id!==_.route.id:!1,u=nn(_.url,t);let p=!1;const f=l.map((y,b)=>{var P;const A=_.branch[b],k=!!(y!=null&&y[0])&&((A==null?void 0:A.loader)!==y[1]||je(p,g,h,u,(P=A.server)==null?void 0:P.uses,r));return k&&(p=!0),k});if(f.some(Boolean)){try{d=await lt(t,f)}catch(y){const b=await D(y,{url:t,params:r,route:{id:e}});return M.has(o)?$e({error:b,url:t,params:r,route:a}):ce({status:Z(y),error:b,url:t,route:a})}if(d.type==="redirect")return d}const m=d==null?void 0:d.nodes;let v=!1;const S=l.map(async(y,b)=>{var le;if(!y)return;const A=_.branch[b],k=m==null?void 0:m[b];if((!k||k.type==="skip")&&y[1]===(A==null?void 0:A.loader)&&!je(v,g,h,u,(le=A.universal)==null?void 0:le.uses,r))return A;if(v=!0,(k==null?void 0:k.type)==="error")throw k;return Ee({loader:y[1],url:t,params:r,route:a,parent:async()=>{var Te;const Ie={};for(let fe=0;fe<b;fe+=1)Object.assign(Ie,(Te=await S[fe])==null?void 0:Te.data);return Ie},server_data_node:Se(k===void 0&&y[0]?{type:"skip"}:k??null,y[0]?A==null?void 0:A.server:void 0)})});for(const y of S)y.catch(()=>{});const w=[];for(let y=0;y<l.length;y+=1)if(l[y])try{w.push(await S[y])}catch(b){if(b instanceof Je)return{type:"redirect",location:b.location};if(M.has(o))return $e({error:await D(b,{params:r,url:t,route:{id:a.id}}),url:t,params:r,route:a});let A=Z(b),k;if(m!=null&&m.includes(b))A=b.status??A,k=b.error;else if(b instanceof se)k=b.body;else{if(await x.updated.check())return await Xe(),await F(t);k=await D(b,{params:r,url:t,route:{id:a.id}})}const P=await rn(y,w,i);return P?re({url:t,params:r,branch:w.slice(0,P.idx).concat(P.node),status:A,error:k,route:a}):await ct(t,{id:a.id},k,A)}else w.push(void 0);return re({url:t,params:r,branch:w,status:200,error:null,route:a,form:n?void 0:null})}async function rn(e,n,t){for(;e--;)if(t[e]){let r=e;for(;!n[r];)r-=1;try{return{idx:r+1,node:{node:await t[e](),loader:t[e],data:{},server:null,universal:null}}}catch{continue}}}async function ce({status:e,error:n,url:t,route:r}){const a={};let o=null;if(V.server_loads[0]===0)try{const l=await lt(t,[!0]);if(l.type!=="data"||l.nodes[0]&&l.nodes[0].type!=="data")throw 0;o=l.nodes[0]??null}catch{(t.origin!==Y||t.pathname!==location.pathname||ve)&&await F(t)}const s=await Ee({loader:ge,url:t,params:a,route:r,parent:()=>Promise.resolve({}),server_data_node:Se(o)}),c={node:await Q(),loader:Q,universal:null,server:null,data:null};return re({url:t,params:a,branch:[s,c],status:e,error:n,route:null})}function Re(e,n){if(!e||oe(e,U))return;let t;try{t=V.hooks.reroute({url:new URL(e)})??e.pathname}catch{return}const r=st(t);for(const a of ie){const o=a.exec(r);if(o)return{id:e.pathname+e.search,invalidating:n,route:a,params:yt(o),url:e}}}function st(e){return mt(e.slice(U.length)||"/")}function it({url:e,type:n,intent:t,delta:r}){let a=!1;const o=ut(_,t,e,n);r!==void 0&&(o.navigation.delta=r);const i={...o.navigation,cancel:()=>{a=!0,o.reject(new Error("navigation cancelled"))}};return W||Qe.forEach(s=>s(i)),a?null:o}async function z({type:e,url:n,popped:t,keepfocus:r,noscroll:a,replace_state:o,state:i={},redirect_count:s=0,nav_token:c={},accept:l=Ce,block:d=Ce}){const h=Re(n,!1),g=it({url:n,type:e,delta:t==null?void 0:t.delta,intent:h});if(!g){d();return}const u=E,p=T;l(),W=!0,te&&x.navigating.set(g.navigation),ne=c;let f=h&&await ot(h);if(!f){if(oe(n,U))return await F(n);f=await ct(n,{id:null},await D(new we(404,"Not Found",`Not found: ${n.pathname}`),{url:n,params:{},route:{id:null}}),404)}if(n=(h==null?void 0:h.url)||n,ne!==c)return g.reject(new Error("navigation aborted")),!1;if(f.type==="redirect")if(s>=20)f=await ce({status:500,error:await D(new Error("Redirect loop"),{url:n,params:{},route:{id:null}}),url:n,route:{id:null}});else return rt(new URL(f.location,n).href,{},s+1,c),!1;else f.props.page.status>=400&&await x.updated.check()&&(await Xe(),await F(n));if(en(),be(u),tt(p),f.props.page.url.pathname!==n.pathname&&(n.pathname=f.props.page.url.pathname),i=t?t.state:i,!t){const w=o?0:1,y={[$]:E+=w,[H]:T+=w,[He]:i};(o?history.replaceState:history.pushState).call(history,y,"",n),o||Zt(E,T)}if(R=null,f.props.page.state=i,te){_=f.state,f.props.page&&(f.props.page.url=n);const w=(await Promise.all(Qt.map(y=>y(g.navigation)))).filter(y=>typeof y=="function");if(w.length>0){let y=function(){j=j.filter(b=>!w.includes(b))};w.push(y),j.push(...w)}ke.$set(f.props),et=!0}else at(f,me,!1);const{activeElement:m}=document;await pt();const v=t?t.scroll:a?_e():null;if(Ne){const w=n.hash&&document.getElementById(decodeURIComponent(n.hash.slice(1)));v?scrollTo(v.x,v.y):w?w.scrollIntoView():scrollTo(0,0)}const S=document.activeElement!==m&&document.activeElement!==document.body;!r&&!S&&ln(),Ne=!0,f.props.page&&(I=f.props.page),W=!1,e==="popstate"&&nt(T),g.fulfil(void 0),j.forEach(w=>w(g.navigation)),x.navigating.set(null)}async function ct(e,n,t,r){return e.origin===Y&&e.pathname===location.pathname&&!ve?await ce({status:r,error:t,url:e,route:n}):await F(e)}function an(){let e;L.addEventListener("mousemove",o=>{const i=o.target;clearTimeout(e),e=setTimeout(()=>{r(i,2)},20)});function n(o){o.defaultPrevented||r(o.composedPath()[0],1)}L.addEventListener("mousedown",n),L.addEventListener("touchstart",n,{passive:!0});const t=new IntersectionObserver(o=>{for(const i of o)i.isIntersecting&&(he(i.target.href),t.unobserve(i.target))},{threshold:0});function r(o,i){const s=Ye(o,L);if(!s)return;const{url:c,external:l,download:d}=pe(s,U);if(l||d)return;const h=X(s),g=c&&_.url.pathname+_.url.search===c.pathname+c.search;if(!h.reload&&!g)if(i<=h.preload_data){const u=Re(c,!1);u&&tn(u)}else i<=h.preload_code&&he(c.pathname)}function a(){t.disconnect();for(const o of L.querySelectorAll("a")){const{url:i,external:s,download:c}=pe(o,U);if(s||c)continue;const l=X(o);l.reload||(l.preload_code===J.viewport&&t.observe(o),l.preload_code===J.eager&&he(i.pathname))}}j.push(a),a()}function D(e,n){if(e instanceof se)return e.body;const t=Z(e),r=Xt(e);return V.hooks.handleError({error:e,event:n,status:t,message:r})??{message:r}}function on(e,n={}){return e=Ke(e),e.origin!==Y?Promise.reject(new Error("goto: invalid URL")):rt(e,n,0)}function sn(){var n;history.scrollRestoration="manual",addEventListener("beforeunload",t=>{let r=!1;if(Oe(),!W){const a=ut(_,void 0,null,"leave"),o={...a.navigation,cancel:()=>{r=!0,a.reject(new Error("navigation cancelled"))}};Qe.forEach(i=>i(o))}r?(t.preventDefault(),t.returnValue=""):history.scrollRestoration="auto"}),addEventListener("visibilitychange",()=>{document.visibilityState==="hidden"&&Oe()}),(n=navigator.connection)!=null&&n.saveData||an(),L.addEventListener("click",async t=>{if(t.button||t.which!==1||t.metaKey||t.ctrlKey||t.shiftKey||t.altKey||t.defaultPrevented)return;const r=Ye(t.composedPath()[0],L);if(!r)return;const{url:a,external:o,target:i,download:s}=pe(r,U);if(!a)return;if(i==="_parent"||i==="_top"){if(window.parent!==window)return}else if(i&&i!=="_self")return;const c=X(r);if(!(r instanceof SVGAElement)&&a.protocol!==location.protocol&&!(a.protocol==="https:"||a.protocol==="http:")||s)return;const[d,h]=a.href.split("#"),g=d===ue(location);if(o||c.reload&&(!g||!h)){it({url:a,type:"link"})?W=!0:t.preventDefault();return}if(h!==void 0&&g){const[,u]=_.url.href.split("#");if(u===h){if(t.preventDefault(),h===""||h==="top"&&r.ownerDocument.getElementById("top")===null)window.scrollTo({top:0});else{const p=r.ownerDocument.getElementById(decodeURIComponent(h));p&&(p.scrollIntoView(),p.focus())}return}if(q=!0,be(E),e(a),!c.replace_state)return;q=!1}t.preventDefault(),await new Promise(u=>{requestAnimationFrame(()=>{setTimeout(u,0)}),setTimeout(u,100)}),z({type:"link",url:a,keepfocus:c.keepfocus,noscroll:c.noscroll,replace_state:c.replace_state??a.href===location.href})}),L.addEventListener("submit",t=>{if(t.defaultPrevented)return;const r=HTMLFormElement.prototype.cloneNode.call(t.target),a=t.submitter;if(((a==null?void 0:a.formTarget)||r.target)==="_blank"||((a==null?void 0:a.formMethod)||r.method)!=="get")return;const s=new URL((a==null?void 0:a.hasAttribute("formaction"))&&(a==null?void 0:a.formAction)||r.action);if(oe(s,U))return;const c=t.target,l=X(c);if(l.reload)return;t.preventDefault(),t.stopPropagation();const d=new FormData(c),h=a==null?void 0:a.getAttribute("name");h&&d.append(h,(a==null?void 0:a.getAttribute("value"))??""),s.search=new URLSearchParams(d).toString(),z({type:"form",url:s,keepfocus:l.keepfocus,noscroll:l.noscroll,replace_state:l.replace_state??s.href===location.href})}),addEventListener("popstate",async t=>{var r;if((r=t.state)!=null&&r[$]){const a=t.state[$];if(ne={},a===E)return;const o=C[a],i=t.state[He]??{},s=new URL(t.state[jt]??location.href),c=t.state[H],l=ue(location)===ue(_.url);if(c===T&&(et||l)){e(s),C[E]=_e(),o&&scrollTo(o.x,o.y),i!==I.state&&(I={...I,state:i},ke.$set({page:I})),E=a;return}const h=a-E;await z({type:"popstate",url:s,popped:{state:i,scroll:o,delta:h},accept:()=>{E=a,T=c},block:()=>{history.go(-h)},nav_token:ne})}else if(!q){const a=new URL(location.href);e(a)}}),addEventListener("hashchange",()=>{q&&(q=!1,history.replaceState({...history.state,[$]:++E,[H]:T},"",location.href))});for(const t of document.querySelectorAll("link"))t.rel==="icon"&&(t.href=t.href);addEventListener("pageshow",t=>{t.persisted&&x.navigating.set(null)});function e(t){_.url=t,x.page.set({...I,url:t}),x.page.notify()}}async function cn(e,{status:n=200,error:t,node_ids:r,params:a,route:o,data:i,form:s}){ve=!0;const c=new URL(location.href);({params:a={},route:o={id:null}}=Re(c,!1)||{});let l;try{const d=r.map(async(u,p)=>{const f=i[p];return f!=null&&f.uses&&(f.uses=ft(f.uses)),Ee({loader:V.nodes[u],url:c,params:a,route:o,parent:async()=>{const m={};for(let v=0;v<p;v+=1)Object.assign(m,(await d[v]).data);return m},server_data_node:Se(f)})}),h=await Promise.all(d),g=ie.find(({id:u})=>u===o.id);if(g){const u=g.layouts;for(let p=0;p<u.length;p++)u[p]||h.splice(p,0,void 0)}l=re({url:c,params:a,branch:h,status:n,error:t,form:s,route:g??null})}catch(d){if(d instanceof Je){await F(new URL(d.location,location.href));return}l=await ce({status:Z(d),error:await D(d,{url:c,params:a,route:o}),url:c,route:o})}l.props.page&&(l.props.page.state={}),at(l,e,!0)}async function lt(e,n){var a;const t=new URL(e);t.pathname=At(e.pathname),e.pathname.endsWith("/")&&t.searchParams.append(Jt,"1"),t.searchParams.append(zt,n.map(o=>o?"1":"0").join(""));const r=await qe(t.href);if(!r.ok){let o;throw(a=r.headers.get("content-type"))!=null&&a.includes("application/json")?o=await r.json():r.status===404?o="Not Found":r.status===500&&(o="Internal Error"),new se(r.status,o)}return new Promise(async o=>{var h;const i=new Map,s=r.body.getReader(),c=new TextDecoder;function l(g){return Kt(g,{Promise:u=>new Promise((p,f)=>{i.set(u,{fulfil:p,reject:f})})})}let d="";for(;;){const{done:g,value:u}=await s.read();if(g&&!d)break;for(d+=!u&&d?`
`:c.decode(u,{stream:!0});;){const p=d.indexOf(`
`);if(p===-1)break;const f=JSON.parse(d.slice(0,p));if(d=d.slice(p+1),f.type==="redirect")return o(f);if(f.type==="data")(h=f.nodes)==null||h.forEach(m=>{(m==null?void 0:m.type)==="data"&&(m.uses=ft(m.uses),m.data=l(m.data))}),o(f);else if(f.type==="chunk"){const{id:m,data:v,error:S}=f,w=i.get(m);i.delete(m),S?w.reject(l(S)):w.fulfil(l(v))}}}})}function ft(e){return{dependencies:new Set((e==null?void 0:e.dependencies)??[]),params:new Set((e==null?void 0:e.params)??[]),parent:!!(e!=null&&e.parent),route:!!(e!=null&&e.route),url:!!(e!=null&&e.url),search_params:new Set((e==null?void 0:e.search_params)??[])}}function ln(){const e=document.querySelector("[autofocus]");if(e)e.focus();else{const n=document.body,t=n.getAttribute("tabindex");n.tabIndex=-1,n.focus({preventScroll:!0,focusVisible:!1}),t!==null?n.setAttribute("tabindex",t):n.removeAttribute("tabindex");const r=getSelection();if(r&&r.type!=="None"){const a=[];for(let o=0;o<r.rangeCount;o+=1)a.push(r.getRangeAt(o));setTimeout(()=>{if(r.rangeCount===a.length){for(let o=0;o<r.rangeCount;o+=1){const i=a[o],s=r.getRangeAt(o);if(i.commonAncestorContainer!==s.commonAncestorContainer||i.startContainer!==s.startContainer||i.endContainer!==s.endContainer||i.startOffset!==s.startOffset||i.endOffset!==s.endOffset)return}r.removeAllRanges()}})}}}function ut(e,n,t,r){var c,l;let a,o;const i=new Promise((d,h)=>{a=d,o=h});return i.catch(()=>{}),{navigation:{from:{params:e.params,route:{id:((c=e.route)==null?void 0:c.id)??null},url:e.url},to:t&&{params:(n==null?void 0:n.params)??null,route:{id:((l=n==null?void 0:n.route)==null?void 0:l.id)??null},url:t},willUnload:!n,type:r,complete:i},fulfil:a,reject:o}}export{Je as R,on as a,U as b,hn as c,un as d,dn as g,x as s,ae as w};
