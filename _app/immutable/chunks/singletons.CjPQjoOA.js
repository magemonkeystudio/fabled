import{z as p,_ as S,x as I,s as N,W as U}from"./scheduler.A46dufu9.js";const f=[];function L(e,t){return{subscribe:_(e,t).subscribe}}function _(e,t=p){let n;const o=new Set;function r(s){if(N(e,s)&&(e=s,n)){const i=!f.length;for(const l of o)l[1](),f.push(l,e);if(i){for(let l=0;l<f.length;l+=2)f[l][0](f[l+1]);f.length=0}}}function c(s){r(s(e))}function a(s,i=p){const l=[s,i];return o.add(l),o.size===1&&(n=t(r,c)||p),s(e),()=>{o.delete(l),o.size===0&&n&&(n(),n=null)}}return{set:r,update:c,subscribe:a}}function G(e,t,n){const o=!Array.isArray(e),r=o?[e]:e;if(!r.every(Boolean))throw new Error("derived() expects stores as input, got a falsy value");const c=t.length<2;return L(n,(a,s)=>{let i=!1;const l=[];let b=0,g=p;const v=()=>{if(b)return;g();const u=t(o?l[0]:l,a,s);c?a(u):g=U(u)?u:p},R=r.map((u,h)=>S(u,T=>{l[h]=T,b&=~(1<<h),i&&v()},()=>{b|=1<<h}));return i=!0,v(),function(){I(R),g(),i=!1}})}const O=globalThis.__sveltekit_13lnjcu?.base??"/proskillapi",x=globalThis.__sveltekit_13lnjcu?.assets??O,Y="1704676826453",K="sveltekit:snapshot",q="sveltekit:scroll",z="sveltekit:states",D="sveltekit:pageurl",H="sveltekit:history",X="sveltekit:navigation",k={tap:1,hover:2,viewport:3,eager:4,off:-1,false:-1},A=location.origin;function $(e){if(e instanceof URL)return e;let t=document.baseURI;if(!t){const n=document.getElementsByTagName("base");t=n.length?n[0].href:document.URL}return new URL(e,t)}function B(){return{x:pageXOffset,y:pageYOffset}}function d(e,t){return e.getAttribute(`data-sveltekit-${t}`)}const m={...k,"":k.hover};function E(e){let t=e.assignedSlot??e.parentNode;return t?.nodeType===11&&(t=t.host),t}function C(e,t){for(;e&&e!==t;){if(e.nodeName.toUpperCase()==="A"&&e.hasAttribute("href"))return e;e=E(e)}}function W(e,t){let n;try{n=new URL(e instanceof SVGAElement?e.href.baseVal:e.href,document.baseURI)}catch{}const o=e instanceof SVGAElement?e.target.baseVal:e.target,r=!n||!!o||P(n,t)||(e.getAttribute("rel")||"").split(/\s+/).includes("external"),c=n?.origin===A&&e.hasAttribute("download");return{url:n,external:r,target:o,download:c}}function F(e){let t=null,n=null,o=null,r=null,c=null,a=null,s=e;for(;s&&s!==document.documentElement;)o===null&&(o=d(s,"preload-code")),r===null&&(r=d(s,"preload-data")),t===null&&(t=d(s,"keepfocus")),n===null&&(n=d(s,"noscroll")),c===null&&(c=d(s,"reload")),a===null&&(a=d(s,"replacestate")),s=E(s);function i(l){switch(l){case"":case"true":return!0;case"off":case"false":return!1;default:return}}return{preload_code:m[o??"off"],preload_data:m[r??"off"],keepfocus:i(t),noscroll:i(n),reload:i(c),replace_state:i(a)}}function y(e){const t=_(e);let n=!0;function o(){n=!0,t.update(a=>a)}function r(a){n=!1,t.set(a)}function c(a){let s;return t.subscribe(i=>{(s===void 0||n&&i!==s)&&a(s=i)})}return{notify:o,set:r,subscribe:c}}function j(){const{set:e,subscribe:t}=_(!1);let n;async function o(){clearTimeout(n);try{const r=await fetch(`${x}/_app/version.json`,{headers:{pragma:"no-cache","cache-control":"no-cache"}});if(!r.ok)return!1;const a=(await r.json()).version!==Y;return a&&(e(!0),clearTimeout(n)),a}catch{return!1}}return{subscribe:t,check:o}}function P(e,t){return e.origin!==A||!e.pathname.startsWith(t)}let w;function J(e){w=e.client}function M(e){return(...t)=>w[e](...t)}const Q={url:y({}),page:y({}),navigating:_(null),updated:j()};export{H,X as N,D as P,q as S,z as a,K as b,F as c,Q as d,O as e,C as f,W as g,k as h,P as i,J as j,G as k,M as l,A as o,$ as r,B as s,_ as w};
