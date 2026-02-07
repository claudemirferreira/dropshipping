import{C as x,q as ut}from"./chunk-SKPXJML6.js";import{a as O}from"./chunk-IUZYJJMP.js";import{$ as ht,A as rt,B as lt,C as pt,Ca as w,F as a,H as V,J as ct,L as k,T as s,g as ot,i as nt,l as at,ma as dt,o as st,v as u,xa as E}from"./chunk-BM7HUZFL.js";import{Ab as T,Ba as Z,Ea as D,Ga as W,Ja as q,Ka as b,La as U,Mb as C,N as g,Na as y,O as v,Pa as L,T as m,Tb as d,Ub as p,Va as _,Wa as l,Y as A,Ya as G,Z as F,Za as J,_ as P,_a as z,a as c,aa as h,cb as S,db as M,eb as K,ga as R,ha as j,ib as Q,kb as X,lb as r,mb as Y,nb as tt,sb as H,tb as et,ub as it,wa as N,za as I}from"./chunk-TPIISR2U.js";var mt=["*"];function bt(e,f){if(e&1&&(S(0,"span",3),et(1),M()),e&2){let t=r();I(),it(t.label)}}function yt(e,f){if(e&1&&K(0,"span",5),e&2){let t=r(2);z(t.icon),l("ngClass","p-avatar-icon")}}function Lt(e,f){if(e&1&&L(0,yt,1,3,"span",4),e&2){let t=r(),i=H(5);l("ngIf",t.icon)("ngIfElse",i)}}function Tt(e,f){if(e&1){let t=Q();S(0,"img",7),X("error",function(o){F(t);let n=r(2);return P(n.imageError(o))}),M()}if(e&2){let t=r(2);l("src",t.image,N),_("aria-label",t.ariaLabel)}}function Et(e,f){if(e&1&&L(0,Tt,1,2,"img",6),e&2){let t=r();l("ngIf",t.image)}}var wt=({dt:e})=>`
.p-avatar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: ${e("avatar.width")};
    height: ${e("avatar.height")};
    font-size: ${e("avatar.font.size")};
    color: ${e("avatar.color")};
    background: ${e("avatar.background")};
    border-radius: ${e("avatar.border.radius")};
}

.p-avatar-image {
    background: transparent;
}

.p-avatar-circle {
    border-radius: 50%;
}

.p-avatar-circle img {
    border-radius: 50%;
}

.p-avatar-icon {
    font-size: ${e("avatar.icon.size")};
    width: ${e("avatar.icon.size")};
    height: ${e("avatar.icon.size")};
}

.p-avatar img {
    width: 100%;
    height: 100%;
}

.p-avatar-lg {
    width: ${e("avatar.lg.width")};
    height: ${e("avatar.lg.width")};
    font-size: ${e("avatar.lg.font.size")};
}

.p-avatar-lg .p-avatar-icon {
    font-size: ${e("avatar.lg.icon.size")};
    width: ${e("avatar.lg.icon.size")};
    height: ${e("avatar.lg.icon.size")};
}

.p-avatar-xl {
    width: ${e("avatar.xl.width")};
    height: ${e("avatar.xl.width")};
    font-size: ${e("avatar.xl.font.size")};
}

.p-avatar-xl .p-avatar-icon {
    font-size: ${e("avatar.xl.font.size")};
    width: ${e("avatar.xl.icon.size")};
    height: ${e("avatar.xl.icon.size")};
}

.p-avatar-group {
    display: flex;
    align-items: center;
}

.p-avatar-group .p-avatar + .p-avatar {
    margin-inline-start: ${e("avatar.group.offset")};
}

.p-avatar-group .p-avatar {
    border: 2px solid ${e("avatar.group.border.color")};
}

.p-avatar-group .p-avatar-lg + .p-avatar-lg {
    margin-inline-start: ${e("avatar.lg.group.offset")};
}

.p-avatar-group .p-avatar-xl + .p-avatar-xl {
    margin-inline-start: ${e("avatar.xl.group.offset")};
}
`,Ot={root:({props:e})=>["p-avatar p-component",{"p-avatar-image":e.image!=null,"p-avatar-circle":e.shape==="circle","p-avatar-lg":e.size==="large","p-avatar-xl":e.size==="xlarge"}],label:"p-avatar-label",icon:"p-avatar-icon"},ft=(()=>{class e extends w{name="avatar";theme=wt;classes=Ot;static \u0275fac=(()=>{let t;return function(o){return(t||(t=h(e)))(o||e)}})();static \u0275prov=g({token:e,factory:e.\u0275fac})}return e})();var xt=(()=>{class e extends O{label;icon;image;size="normal";shape="square";style;styleClass;ariaLabel;ariaLabelledBy;onImageError=new R;_componentStyle=m(ft);imageError(t){this.onImageError.emit(t)}get hostClass(){return this.styleClass}static \u0275fac=(()=>{let t;return function(o){return(t||(t=h(e)))(o||e)}})();static \u0275cmp=q({type:e,selectors:[["p-avatar"]],hostVars:19,hostBindings:function(i,o){i&2&&(_("data-pc-name","avatar")("aria-label",o.ariaLabel)("aria-labelledby",o.ariaLabelledBy),J(o.style),z(o.hostClass),G("p-avatar",!0)("p-component",!0)("p-avatar-circle",o.shape==="circle")("p-avatar-lg",o.size==="large")("p-avatar-xl",o.size==="xlarge")("p-avatar-image",o.image!=null))},inputs:{label:"label",icon:"icon",image:"image",size:"size",shape:"shape",style:"style",styleClass:"styleClass",ariaLabel:"ariaLabel",ariaLabelledBy:"ariaLabelledBy"},outputs:{onImageError:"onImageError"},features:[T([ft]),y],ngContentSelectors:mt,decls:6,vars:2,consts:[["iconTemplate",""],["imageTemplate",""],["class","p-avatar-text",4,"ngIf","ngIfElse"],[1,"p-avatar-text"],[3,"class","ngClass",4,"ngIf","ngIfElse"],[3,"ngClass"],[3,"src","error",4,"ngIf"],[3,"error","src"]],template:function(i,o){if(i&1&&(Y(),tt(0),L(1,bt,2,1,"span",2)(2,Lt,1,2,"ng-template",null,0,C)(4,Et,1,1,"ng-template",null,1,C)),i&2){let n=H(3);I(),l("ngIf",o.label)("ngIfElse",n)}},dependencies:[at,ot,nt,E],encapsulation:2,changeDetection:0})}return e})(),jt=(()=>{class e{static \u0275fac=function(i){return new(i||e)};static \u0275mod=b({type:e});static \u0275inj=v({imports:[xt,E,E]})}return e})();var $t=({dt:e})=>`
.p-tooltip {
    position: absolute;
    display: none;
    max-width: ${e("tooltip.max.width")};
}

.p-tooltip-right,
.p-tooltip-left {
    padding: 0 ${e("tooltip.gutter")};
}

.p-tooltip-top,
.p-tooltip-bottom {
    padding: ${e("tooltip.gutter")} 0;
}

.p-tooltip-text {
    white-space: pre-line;
    word-break: break-word;
    background: ${e("tooltip.background")};
    color: ${e("tooltip.color")};
    padding: ${e("tooltip.padding")};
    box-shadow: ${e("tooltip.shadow")};
    border-radius: ${e("tooltip.border.radius")};
}

.p-tooltip-arrow {
    position: absolute;
    width: 0;
    height: 0;
    border-color: transparent;
    border-style: solid;
    scale: 2;
}

.p-tooltip-right .p-tooltip-arrow {
    top: 50%;
    left: 0;
    margin-top: calc(-1 * ${e("tooltip.gutter")});
    border-width: ${e("tooltip.gutter")} ${e("tooltip.gutter")} ${e("tooltip.gutter")} 0;
    border-right-color: ${e("tooltip.background")};
}

.p-tooltip-left .p-tooltip-arrow {
    top: 50%;
    right: 0;
    margin-top: calc(-1 * ${e("tooltip.gutter")});
    border-width: ${e("tooltip.gutter")} 0 ${e("tooltip.gutter")} ${e("tooltip.gutter")};
    border-left-color: ${e("tooltip.background")};
}

.p-tooltip-top .p-tooltip-arrow {
    bottom: 0;
    left: 50%;
    margin-left: calc(-1 * ${e("tooltip.gutter")});
    border-width: ${e("tooltip.gutter")} ${e("tooltip.gutter")} 0 ${e("tooltip.gutter")};
    border-top-color: ${e("tooltip.background")};
    border-bottom-color: ${e("tooltip.background")};
}

.p-tooltip-bottom .p-tooltip-arrow {
    top: 0;
    left: 50%;
    margin-left: calc(-1 * ${e("tooltip.gutter")});
    border-width: 0 ${e("tooltip.gutter")} ${e("tooltip.gutter")} ${e("tooltip.gutter")};
    border-top-color: ${e("tooltip.background")};
    border-bottom-color: ${e("tooltip.background")};
}
`,It={root:"p-tooltip p-component",arrow:"p-tooltip-arrow",text:"p-tooltip-text"},gt=(()=>{class e extends w{name="tooltip";theme=$t;classes=It;static \u0275fac=(()=>{let t;return function(o){return(t||(t=h(e)))(o||e)}})();static \u0275prov=g({token:e,factory:e.\u0275fac})}return e})();var ee=(()=>{class e extends O{zone;viewContainer;tooltipPosition;tooltipEvent="hover";appendTo;positionStyle;tooltipStyleClass;tooltipZIndex;escape=!0;showDelay;hideDelay;life;positionTop;positionLeft;autoHide=!0;fitContent=!0;hideOnEscape=!0;content;get disabled(){return this._disabled}set disabled(t){this._disabled=t,this.deactivate()}tooltipOptions;_tooltipOptions={tooltipLabel:null,tooltipPosition:"right",tooltipEvent:"hover",appendTo:"body",positionStyle:null,tooltipStyleClass:null,tooltipZIndex:"auto",escape:!0,disabled:null,showDelay:null,hideDelay:null,positionTop:null,positionLeft:null,life:null,autoHide:!0,hideOnEscape:!0,id:dt("pn_id_")+"_tooltip"};_disabled;container;styleClass;tooltipText;showTimeout;hideTimeout;active;mouseEnterListener;mouseLeaveListener;containerMouseleaveListener;clickListener;focusListener;blurListener;documentEscapeListener;scrollHandler;resizeListener;_componentStyle=m(gt);interactionInProgress=!1;constructor(t,i){super(),this.zone=t,this.viewContainer=i}ngAfterViewInit(){super.ngAfterViewInit(),st(this.platformId)&&this.zone.runOutsideAngular(()=>{let t=this.getOption("tooltipEvent");if((t==="hover"||t==="both")&&(this.mouseEnterListener=this.onMouseEnter.bind(this),this.mouseLeaveListener=this.onMouseLeave.bind(this),this.clickListener=this.onInputClick.bind(this),this.el.nativeElement.addEventListener("mouseenter",this.mouseEnterListener),this.el.nativeElement.addEventListener("click",this.clickListener),this.el.nativeElement.addEventListener("mouseleave",this.mouseLeaveListener)),t==="focus"||t==="both"){this.focusListener=this.onFocus.bind(this),this.blurListener=this.onBlur.bind(this);let i=this.el.nativeElement.querySelector(".p-component");i||(i=this.getTarget(this.el.nativeElement)),i.addEventListener("focus",this.focusListener),i.addEventListener("blur",this.blurListener)}})}ngOnChanges(t){super.ngOnChanges(t),t.tooltipPosition&&this.setOption({tooltipPosition:t.tooltipPosition.currentValue}),t.tooltipEvent&&this.setOption({tooltipEvent:t.tooltipEvent.currentValue}),t.appendTo&&this.setOption({appendTo:t.appendTo.currentValue}),t.positionStyle&&this.setOption({positionStyle:t.positionStyle.currentValue}),t.tooltipStyleClass&&this.setOption({tooltipStyleClass:t.tooltipStyleClass.currentValue}),t.tooltipZIndex&&this.setOption({tooltipZIndex:t.tooltipZIndex.currentValue}),t.escape&&this.setOption({escape:t.escape.currentValue}),t.showDelay&&this.setOption({showDelay:t.showDelay.currentValue}),t.hideDelay&&this.setOption({hideDelay:t.hideDelay.currentValue}),t.life&&this.setOption({life:t.life.currentValue}),t.positionTop&&this.setOption({positionTop:t.positionTop.currentValue}),t.positionLeft&&this.setOption({positionLeft:t.positionLeft.currentValue}),t.disabled&&this.setOption({disabled:t.disabled.currentValue}),t.content&&(this.setOption({tooltipLabel:t.content.currentValue}),this.active&&(t.content.currentValue?this.container&&this.container.offsetParent?(this.updateText(),this.align()):this.show():this.hide())),t.autoHide&&this.setOption({autoHide:t.autoHide.currentValue}),t.id&&this.setOption({id:t.id.currentValue}),t.tooltipOptions&&(this._tooltipOptions=c(c({},this._tooltipOptions),t.tooltipOptions.currentValue),this.deactivate(),this.active&&(this.getOption("tooltipLabel")?this.container&&this.container.offsetParent?(this.updateText(),this.align()):this.show():this.hide()))}isAutoHide(){return this.getOption("autoHide")}onMouseEnter(t){!this.container&&!this.showTimeout&&this.activate()}onMouseLeave(t){this.isAutoHide()?this.deactivate():!(u(t.relatedTarget,"p-tooltip")||u(t.relatedTarget,"p-tooltip-text")||u(t.relatedTarget,"p-tooltip-arrow"))&&this.deactivate()}onFocus(t){this.activate()}onBlur(t){this.deactivate()}onInputClick(t){this.deactivate()}activate(){if(!this.interactionInProgress){if(this.active=!0,this.clearHideTimeout(),this.getOption("showDelay")?this.showTimeout=setTimeout(()=>{this.show()},this.getOption("showDelay")):this.show(),this.getOption("life")){let t=this.getOption("showDelay")?this.getOption("life")+this.getOption("showDelay"):this.getOption("life");this.hideTimeout=setTimeout(()=>{this.hide()},t)}this.getOption("hideOnEscape")&&(this.documentEscapeListener=this.renderer.listen("document","keydown.escape",()=>{this.deactivate(),this.documentEscapeListener()})),this.interactionInProgress=!0}}deactivate(){this.interactionInProgress=!1,this.active=!1,this.clearShowTimeout(),this.getOption("hideDelay")?(this.clearHideTimeout(),this.hideTimeout=setTimeout(()=>{this.hide()},this.getOption("hideDelay"))):this.hide(),this.documentEscapeListener&&this.documentEscapeListener()}create(){this.container&&(this.clearHideTimeout(),this.remove()),this.container=document.createElement("div"),this.container.setAttribute("id",this.getOption("id")),this.container.setAttribute("role","tooltip");let t=document.createElement("div");t.className="p-tooltip-arrow",this.container.appendChild(t),this.tooltipText=document.createElement("div"),this.tooltipText.className="p-tooltip-text",this.updateText(),this.getOption("positionStyle")&&(this.container.style.position=this.getOption("positionStyle")),this.container.appendChild(this.tooltipText),this.getOption("appendTo")==="body"?document.body.appendChild(this.container):this.getOption("appendTo")==="target"?V(this.container,this.el.nativeElement):V(this.getOption("appendTo"),this.container),this.container.style.display="none",this.fitContent&&(this.container.style.width="fit-content"),this.isAutoHide()?this.container.style.pointerEvents="none":(this.container.style.pointerEvents="unset",this.bindContainerMouseleaveListener())}bindContainerMouseleaveListener(){if(!this.containerMouseleaveListener){let t=this.container??this.container.nativeElement;this.containerMouseleaveListener=this.renderer.listen(t,"mouseleave",i=>{this.deactivate()})}}unbindContainerMouseleaveListener(){this.containerMouseleaveListener&&(this.bindContainerMouseleaveListener(),this.containerMouseleaveListener=null)}show(){if(!this.getOption("tooltipLabel")||this.getOption("disabled"))return;this.create(),this.el.nativeElement.closest("p-dialog")?setTimeout(()=>{this.container&&(this.container.style.display="inline-block"),this.container&&this.align()},100):(this.container.style.display="inline-block",this.align()),ct(this.container,250),this.getOption("tooltipZIndex")==="auto"?x.set("tooltip",this.container,this.config.zIndex.tooltip):this.container.style.zIndex=this.getOption("tooltipZIndex"),this.bindDocumentResizeListener(),this.bindScrollListener()}hide(){this.getOption("tooltipZIndex")==="auto"&&x.clear(this.container),this.remove()}updateText(){let t=this.getOption("tooltipLabel");if(t instanceof Z){let i=this.viewContainer.createEmbeddedView(t);i.detectChanges(),i.rootNodes.forEach(o=>this.tooltipText.appendChild(o))}else this.getOption("escape")?(this.tooltipText.innerHTML="",this.tooltipText.appendChild(document.createTextNode(t))):this.tooltipText.innerHTML=t}align(){let t=this.getOption("tooltipPosition"),i={top:[this.alignTop,this.alignBottom,this.alignRight,this.alignLeft],bottom:[this.alignBottom,this.alignTop,this.alignRight,this.alignLeft],left:[this.alignLeft,this.alignRight,this.alignTop,this.alignBottom],right:[this.alignRight,this.alignLeft,this.alignTop,this.alignBottom]};for(let[o,n]of i[t].entries())if(o===0)n.call(this);else if(this.isOutOfBounds())n.call(this);else break}getHostOffset(){if(this.getOption("appendTo")==="body"||this.getOption("appendTo")==="target"){let t=this.el.nativeElement.getBoundingClientRect(),i=t.left+lt(),o=t.top+pt();return{left:i,top:o}}else return{left:0,top:0}}get activeElement(){return this.el.nativeElement.nodeName.startsWith("P-")?k(this.el.nativeElement,".p-component"):this.el.nativeElement}alignRight(){this.preAlign("right");let t=this.activeElement,i=a(t),o=(s(t)-s(this.container))/2;this.alignTooltip(i,o)}alignLeft(){this.preAlign("left");let t=a(this.container),i=(s(this.el.nativeElement)-s(this.container))/2;this.alignTooltip(-t,i)}alignTop(){this.preAlign("top");let t=(a(this.el.nativeElement)-a(this.container))/2,i=s(this.container);this.alignTooltip(t,-i)}alignBottom(){this.preAlign("bottom");let t=(a(this.el.nativeElement)-a(this.container))/2,i=s(this.el.nativeElement);this.alignTooltip(t,i)}alignTooltip(t,i){let o=this.getHostOffset(),n=o.left+t,$=o.top+i;this.container.style.left=n+this.getOption("positionLeft")+"px",this.container.style.top=$+this.getOption("positionTop")+"px"}setOption(t){this._tooltipOptions=c(c({},this._tooltipOptions),t)}getOption(t){return this._tooltipOptions[t]}getTarget(t){return u(t,"p-inputwrapper")?k(t,"input"):t}preAlign(t){this.container.style.left="-999px",this.container.style.top="-999px";let i="p-tooltip p-component p-tooltip-"+t;this.container.className=this.getOption("tooltipStyleClass")?i+" "+this.getOption("tooltipStyleClass"):i}isOutOfBounds(){let t=this.container.getBoundingClientRect(),i=t.top,o=t.left,n=a(this.container),$=s(this.container),B=rt();return o+n>B.width||o<0||i<0||i+$>B.height}onWindowResize(t){this.hide()}bindDocumentResizeListener(){this.zone.runOutsideAngular(()=>{this.resizeListener=this.onWindowResize.bind(this),window.addEventListener("resize",this.resizeListener)})}unbindDocumentResizeListener(){this.resizeListener&&(window.removeEventListener("resize",this.resizeListener),this.resizeListener=null)}bindScrollListener(){this.scrollHandler||(this.scrollHandler=new ut(this.el.nativeElement,()=>{this.container&&this.hide()})),this.scrollHandler.bindScrollListener()}unbindScrollListener(){this.scrollHandler&&this.scrollHandler.unbindScrollListener()}unbindEvents(){let t=this.getOption("tooltipEvent");if((t==="hover"||t==="both")&&(this.el.nativeElement.removeEventListener("mouseenter",this.mouseEnterListener),this.el.nativeElement.removeEventListener("mouseleave",this.mouseLeaveListener),this.el.nativeElement.removeEventListener("click",this.clickListener)),t==="focus"||t==="both"){let i=this.el.nativeElement.querySelector(".p-component");i||(i=this.getTarget(this.el.nativeElement)),i.removeEventListener("focus",this.focusListener),i.removeEventListener("blur",this.blurListener)}this.unbindDocumentResizeListener()}remove(){this.container&&this.container.parentElement&&(this.getOption("appendTo")==="body"?document.body.removeChild(this.container):this.getOption("appendTo")==="target"?this.el.nativeElement.removeChild(this.container):ht(this.getOption("appendTo"),this.container)),this.unbindDocumentResizeListener(),this.unbindScrollListener(),this.unbindContainerMouseleaveListener(),this.clearTimeouts(),this.container=null,this.scrollHandler=null}clearShowTimeout(){this.showTimeout&&(clearTimeout(this.showTimeout),this.showTimeout=null)}clearHideTimeout(){this.hideTimeout&&(clearTimeout(this.hideTimeout),this.hideTimeout=null)}clearTimeouts(){this.clearShowTimeout(),this.clearHideTimeout()}ngOnDestroy(){this.unbindEvents(),super.ngOnDestroy(),this.container&&x.clear(this.container),this.remove(),this.scrollHandler&&(this.scrollHandler.destroy(),this.scrollHandler=null),this.documentEscapeListener&&this.documentEscapeListener()}static \u0275fac=function(i){return new(i||e)(D(j),D(W))};static \u0275dir=U({type:e,selectors:[["","pTooltip",""]],inputs:{tooltipPosition:"tooltipPosition",tooltipEvent:"tooltipEvent",appendTo:"appendTo",positionStyle:"positionStyle",tooltipStyleClass:"tooltipStyleClass",tooltipZIndex:"tooltipZIndex",escape:[2,"escape","escape",d],showDelay:[2,"showDelay","showDelay",p],hideDelay:[2,"hideDelay","hideDelay",p],life:[2,"life","life",p],positionTop:[2,"positionTop","positionTop",p],positionLeft:[2,"positionLeft","positionLeft",p],autoHide:[2,"autoHide","autoHide",d],fitContent:[2,"fitContent","fitContent",d],hideOnEscape:[2,"hideOnEscape","hideOnEscape",d],content:[0,"pTooltip","content"],disabled:[0,"tooltipDisabled","disabled"],tooltipOptions:"tooltipOptions"},features:[T([gt]),y,A]})}return e})(),ie=(()=>{class e{static \u0275fac=function(i){return new(i||e)};static \u0275mod=b({type:e});static \u0275inj=v({})}return e})();export{xt as a,jt as b,ee as c,ie as d};
