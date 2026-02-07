import{a as ye,b as $e}from"./chunk-BB6FFYSY.js";import{d as oe,h as te}from"./chunk-NHH5DZPK.js";import{a as de,b as xe,c as ve}from"./chunk-KGBRAIA6.js";import{c as se,d as S,f as E,h as P}from"./chunk-F2X3VGZZ.js";import{b as ie,c as M,e as ae,f as re,i as le,k as ce,l as me,m as ge,o as pe,v as fe,w as _e,x as be,y as he,z as Ce}from"./chunk-SKPXJML6.js";import{a as ue}from"./chunk-IUZYJJMP.js";import{Ca as ne,g as X,i as Z,k as ee,l as $,wa as w,xa as T}from"./chunk-BM7HUZFL.js";import{$a as g,Ab as K,Cb as z,Db as U,Ja as x,Ka as A,Mb as W,N as D,Na as V,O as N,Pa as m,T as _,Tb as O,Va as I,Wa as a,Z as L,_ as R,aa as k,cb as c,db as r,eb as u,ga as j,hb as v,ib as q,kb as y,la as B,lb as l,mb as G,nb as H,ob as b,qb as h,rb as C,sb as Y,tb as d,ub as J,va as Q,za as i}from"./chunk-TPIISR2U.js";var Ie=["container"],ze=["icon"],Oe=["closeicon"],Se=["*"],Ee=(e,n)=>({showTransitionParams:e,hideTransitionParams:n}),Pe=e=>({value:"visible()",params:e}),Fe=e=>({closeCallback:e});function De(e,n){e&1&&v(0)}function Ne(e,n){if(e&1&&m(0,De,1,0,"ng-container",7),e&2){let o=l(2);a("ngTemplateOutlet",o.iconTemplate||o.iconTemplate)}}function Le(e,n){if(e&1&&u(0,"i",3),e&2){let o=l(2);a("ngClass",o.icon)}}function Re(e,n){if(e&1&&u(0,"span",9),e&2){let o=l(3);a("ngClass",o.cx("text"))("innerHTML",o.text,Q)}}function je(e,n){if(e&1&&(c(0,"div"),m(1,Re,1,2,"span",8),r()),e&2){let o=l(2);i(),a("ngIf",!o.escape)}}function Be(e,n){if(e&1&&(c(0,"span",5),d(1),r()),e&2){let o=l(3);a("ngClass",o.cx("text")),i(),J(o.text)}}function Qe(e,n){if(e&1&&m(0,Be,2,2,"span",10),e&2){let o=l(2);a("ngIf",o.escape&&o.text)}}function Ae(e,n){e&1&&v(0)}function Ve(e,n){if(e&1&&m(0,Ae,1,0,"ng-container",11),e&2){let o=l(2);a("ngTemplateOutlet",o.containerTemplate||o.containerTemplate)("ngTemplateOutletContext",z(2,Fe,o.close.bind(o)))}}function qe(e,n){if(e&1&&(c(0,"span",5),H(1),r()),e&2){let o=l(2);a("ngClass",o.cx("text"))}}function Ge(e,n){if(e&1&&u(0,"i",13),e&2){let o=l(3);a("ngClass",o.closeIcon)}}function He(e,n){e&1&&v(0)}function Ye(e,n){if(e&1&&m(0,He,1,0,"ng-container",7),e&2){let o=l(3);a("ngTemplateOutlet",o.closeIconTemplate||o._closeIconTemplate)}}function Je(e,n){e&1&&u(0,"TimesIcon",14)}function Ke(e,n){if(e&1){let o=q();c(0,"button",12),y("click",function(t){L(o);let p=l(2);return R(p.close(t))}),m(1,Ge,1,1,"i",13)(2,Ye,1,1,"ng-container")(3,Je,1,0,"TimesIcon",14),r()}if(e&2){let o=l(2);I("aria-label",o.closeAriaLabel),i(),g(o.closeIcon?1:-1),i(),g(o.closeIconTemplate||o._closeIconTemplate?2:-1),i(),g(!o.closeIconTemplate&&!o._closeIconTemplate&&!o.closeIcon?3:-1)}}function Ue(e,n){if(e&1&&(c(0,"div",1)(1,"div",2),m(2,Ne,1,1,"ng-container")(3,Le,1,1,"i",3)(4,je,2,1,"div",4)(5,Qe,1,1,"ng-template",null,0,W)(7,Ve,1,4,"ng-container")(8,qe,2,1,"span",5)(9,Ke,4,4,"button",6),r()()),e&2){let o=Y(6),s=l();a("ngClass",s.containerClass)("@messageAnimation",z(13,Pe,U(10,Ee,s.showTransitionOptions,s.hideTransitionOptions))),I("aria-live","polite")("role","alert"),i(2),g(s.iconTemplate||s._iconTemplate?2:-1),i(),g(s.icon?3:-1),i(),a("ngIf",!s.escape)("ngIfElse",o),i(3),g(s.containerTemplate||s._containerTemplate?7:8),i(2),g(s.closable?9:-1)}}var We=({dt:e})=>`
.p-message {
    border-radius: ${e("message.border.radius")};
    outline-width: ${e("message.border.width")};
    outline-style: solid;
}

.p-message-content {
    display: flex;
    align-items: center;
    padding: ${e("message.content.padding")};
    gap: ${e("message.content.gap")};
    height: 100%;
}

.p-message-icon {
    flex-shrink: 0;
}

.p-message-close-button {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin-inline-start: auto;
    overflow: hidden;
    position: relative;
    width: ${e("message.close.button.width")};
    height: ${e("message.close.button.height")};
    border-radius: ${e("message.close.button.border.radius")};
    background: transparent;
    transition: background ${e("message.transition.duration")}, color ${e("message.transition.duration")}, outline-color ${e("message.transition.duration")}, box-shadow ${e("message.transition.duration")}, opacity 0.3s;
    outline-color: transparent;
    color: inherit;
    padding: 0;
    border: none;
    cursor: pointer;
    user-select: none;
}

.p-message-close-icon {
    font-size: ${e("message.close.icon.size")};
    width: ${e("message.close.icon.size")};
    height: ${e("message.close.icon.size")};
}

.p-message-close-button:focus-visible {
    outline-width: ${e("message.close.button.focus.ring.width")};
    outline-style: ${e("message.close.button.focus.ring.style")};
    outline-offset: ${e("message.close.button.focus.ring.offset")};
}

.p-message-info {
    background: ${e("message.info.background")};
    outline-color: ${e("message.info.border.color")};
    color: ${e("message.info.color")};
    box-shadow: ${e("message.info.shadow")};
}

.p-message-info .p-message-close-button:focus-visible {
    outline-color: ${e("message.info.close.button.focus.ring.color")};
    box-shadow: ${e("message.info.close.button.focus.ring.shadow")};
}

.p-message-info .p-message-close-button:hover {
    background: ${e("message.info.close.button.hover.background")};
}

.p-message-info.p-message-outlined {
    color: ${e("message.info.outlined.color")};
    outline-color: ${e("message.info.outlined.border.color")};
}

.p-message-info.p-message-simple {
    color: ${e("message.info.simple.color")};
}

.p-message-success {
    background: ${e("message.success.background")};
    outline-color: ${e("message.success.border.color")};
    color: ${e("message.success.color")};
    box-shadow: ${e("message.success.shadow")};
}

.p-message-success .p-message-close-button:focus-visible {
    outline-color: ${e("message.success.close.button.focus.ring.color")};
    box-shadow: ${e("message.success.close.button.focus.ring.shadow")};
}

.p-message-success .p-message-close-button:hover {
    background: ${e("message.success.close.button.hover.background")};
}

.p-message-success.p-message-outlined {
    color: ${e("message.success.outlined.color")};
    outline-color: ${e("message.success.outlined.border.color")};
}

.p-message-success.p-message-simple {
    color: ${e("message.success.simple.color")};
}

.p-message-warn {
    background: ${e("message.warn.background")};
    outline-color: ${e("message.warn.border.color")};
    color: ${e("message.warn.color")};
    box-shadow: ${e("message.warn.shadow")};
}

.p-message-warn .p-message-close-button:focus-visible {
    outline-color: ${e("message.warn.close.button.focus.ring.color")};
    box-shadow: ${e("message.warn.close.button.focus.ring.shadow")};
}

.p-message-warn .p-message-close-button:hover {
    background: ${e("message.warn.close.button.hover.background")};
}

.p-message-warn.p-message-outlined {
    color: ${e("message.warn.outlined.color")};
    outline-color: ${e("message.warn.outlined.border.color")};
}

.p-message-warn.p-message-simple {
    color: ${e("message.warn.simple.color")};
}

.p-message-error {
    background: ${e("message.error.background")};
    outline-color: ${e("message.error.border.color")};
    color: ${e("message.error.color")};
    box-shadow: ${e("message.error.shadow")};
}

.p-message-error .p-message-close-button:focus-visible {
    outline-color: ${e("message.error.close.button.focus.ring.color")};
    box-shadow: ${e("message.error.close.button.focus.ring.shadow")};
}

.p-message-error .p-message-close-button:hover {
    background: ${e("message.error.close.button.hover.background")};
}

.p-message-error.p-message-outlined {
    color: ${e("message.error.outlined.color")};
    outline-color: ${e("message.error.outlined.border.color")};
}

.p-message-error.p-message-simple {
    color: ${e("message.error.simple.color")};
}

.p-message-secondary {
    background: ${e("message.secondary.background")};
    outline-color: ${e("message.secondary.border.color")};
    color: ${e("message.secondary.color")};
    box-shadow: ${e("message.secondary.shadow")};
}

.p-message-secondary .p-message-close-button:focus-visible {
    outline-color: ${e("message.secondary.close.button.focus.ring.color")};
    box-shadow: ${e("message.secondary.close.button.focus.ring.shadow")};
}

.p-message-secondary .p-message-close-button:hover {
    background: ${e("message.secondary.close.button.hover.background")};
}

.p-message-secondary.p-message-outlined {
    color: ${e("message.secondary.outlined.color")};
    outline-color: ${e("message.secondary.outlined.border.color")};
}

.p-message-secondary.p-message-simple {
    color: ${e("message.secondary.simple.color")};
}

.p-message-contrast {
    background: ${e("message.contrast.background")};
    outline-color: ${e("message.contrast.border.color")};
    color: ${e("message.contrast.color")};
    box-shadow: ${e("message.contrast.shadow")};
}

.p-message-contrast .p-message-close-button:focus-visible {
    outline-color: ${e("message.contrast.close.button.focus.ring.color")};
    box-shadow: ${e("message.contrast.close.button.focus.ring.shadow")};
}

.p-message-contrast .p-message-close-button:hover {
    background: ${e("message.contrast.close.button.hover.background")};
}

.p-message-contrast.p-message-outlined {
    color: ${e("message.contrast.outlined.color")};
    outline-color: ${e("message.contrast.outlined.border.color")};
}

.p-message-contrast.p-message-simple {
    color: ${e("message.contrast.simple.color")};
}

.p-message-text {
    display: inline-flex;
    align-items: center;
    font-size: ${e("message.text.font.size")};
    font-weight: ${e("message.text.font.weight")};
}

.p-message-icon {
    font-size: ${e("message.icon.size")};
    width: ${e("message.icon.size")};
    height: ${e("message.icon.size")};
}

.p-message-enter-from {
    opacity: 0;
}

.p-message-enter-active {
    transition: opacity 0.3s;
}

.p-message.p-message-leave-from {
    max-height: 1000px;
}

.p-message.p-message-leave-to {
    max-height: 0;
    opacity: 0;
    margin: 0;
}

.p-message-leave-active {
    overflow: hidden;
    transition: max-height 0.45s cubic-bezier(0, 1, 0, 1), opacity 0.3s, margin 0.3s;
}

.p-message-leave-active .p-message-close-button {
    opacity: 0;
}

.p-message-sm .p-message-content {
    padding: ${e("message.content.sm.padding")};
}

.p-message-sm .p-message-text {
    font-size: ${e("message.text.sm.font.size")};
}

.p-message-sm .p-message-icon {
    font-size: ${e("message.icon.sm.size")};
    width: ${e("message.icon.sm.size")};
    height: ${e("message.icon.sm.size")};
}

.p-message-sm .p-message-close-icon {
    font-size: ${e("message.close.icon.sm.size")};
    width: ${e("message.close.icon.sm.size")};
    height: ${e("message.close.icon.sm.size")};
}

.p-message-lg .p-message-content {
    padding: ${e("message.content.lg.padding")};
}

.p-message-lg .p-message-text {
    font-size: ${e("message.text.lg.font.size")};
}

.p-message-lg .p-message-icon {
    font-size: ${e("message.icon.lg.size")};
    width: ${e("message.icon.lg.size")};
    height: ${e("message.icon.lg.size")};
}

.p-message-lg .p-message-close-icon {
    font-size: ${e("message.close.icon.lg.size")};
    width: ${e("message.close.icon.lg.size")};
    height: ${e("message.close.icon.lg.size")};
}

.p-message-outlined {
    background: transparent;
    outline-width: ${e("message.outlined.border.width")};
}

.p-message-simple {
    background: transparent;
    outline-color: transparent;
    box-shadow: none;
}

.p-message-simple .p-message-content {
    padding: ${e("message.simple.content.padding")};
}

.p-message-outlined .p-message-close-button:hover,
.p-message-simple .p-message-close-button:hover {
    background: transparent;
}`,Xe={root:({props:e})=>["p-message p-component p-message-"+e.severity,{"p-message-simple":e.variant==="simple"}],content:"p-message-content",icon:"p-message-icon",text:"p-message-text",closeButton:"p-message-close-button",closeIcon:"p-message-close-icon"},we=(()=>{class e extends ne{name="message";theme=We;classes=Xe;static \u0275fac=(()=>{let o;return function(t){return(o||(o=k(e)))(t||e)}})();static \u0275prov=D({token:e,factory:e.\u0275fac})}return e})();var F=(()=>{class e extends ue{severity="info";text;escape=!0;style;styleClass;closable=!1;icon;closeIcon;life;showTransitionOptions="300ms ease-out";hideTransitionOptions="200ms cubic-bezier(0.86, 0, 0.07, 1)";size;variant;onClose=new j;get closeAriaLabel(){return this.config.translation.aria?this.config.translation.aria.close:void 0}get containerClass(){let o=this.variant==="outlined"?"p-message-outlined":this.variant==="simple"?"p-message-simple":"",s=this.size==="small"?"p-message-sm":this.size==="large"?"p-message-lg":"";return`p-message-${this.severity} ${o} ${s}`.trim()+(this.styleClass?" "+this.styleClass:"")}visible=B(!0);_componentStyle=_(we);containerTemplate;iconTemplate;closeIconTemplate;templates;_containerTemplate;_iconTemplate;_closeIconTemplate;ngOnInit(){super.ngOnInit(),this.life&&setTimeout(()=>{this.visible.set(!1)},this.life)}ngAfterContentInit(){this.templates?.forEach(o=>{switch(o.getType()){case"container":this._containerTemplate=o.template;break;case"icon":this._iconTemplate=o.template;break;case"closeicon":this._closeIconTemplate=o.template;break}})}close(o){this.visible.set(!1),this.onClose.emit({originalEvent:o})}static \u0275fac=(()=>{let o;return function(t){return(o||(o=k(e)))(t||e)}})();static \u0275cmp=x({type:e,selectors:[["p-message"]],contentQueries:function(s,t,p){if(s&1&&(b(p,Ie,4),b(p,ze,4),b(p,Oe,4),b(p,w,4)),s&2){let f;h(f=C())&&(t.containerTemplate=f.first),h(f=C())&&(t.iconTemplate=f.first),h(f=C())&&(t.closeIconTemplate=f.first),h(f=C())&&(t.templates=f)}},inputs:{severity:"severity",text:"text",escape:[2,"escape","escape",O],style:"style",styleClass:"styleClass",closable:[2,"closable","closable",O],icon:"icon",closeIcon:"closeIcon",life:"life",showTransitionOptions:"showTransitionOptions",hideTransitionOptions:"hideTransitionOptions",size:"size",variant:"variant"},outputs:{onClose:"onClose"},features:[K([we]),V],ngContentSelectors:Se,decls:1,vars:1,consts:[["escapeOut",""],[1,"p-message","p-component",3,"ngClass"],[1,"p-message-content"],[1,"p-message-icon",3,"ngClass"],[4,"ngIf","ngIfElse"],[3,"ngClass"],["pRipple","","type","button",1,"p-message-close-button"],[4,"ngTemplateOutlet"],[3,"ngClass","innerHTML",4,"ngIf"],[3,"ngClass","innerHTML"],[3,"ngClass",4,"ngIf"],[4,"ngTemplateOutlet","ngTemplateOutletContext"],["pRipple","","type","button",1,"p-message-close-button",3,"click"],[1,"p-message-close-icon",3,"ngClass"],["styleClass","p-message-close-icon"]],template:function(s,t){s&1&&(G(),m(0,Ue,10,15,"div",1)),s&2&&g(t.visible()?0:-1)},dependencies:[$,X,Z,ee,de,fe,T],encapsulation:2,data:{animation:[se("messageAnimation",[P(":enter",[E({opacity:0,transform:"translateY(-25%)"}),S("{{showTransitionParams}}")]),P(":leave",[S("{{hideTransitionParams}}",E({height:0,marginTop:0,marginBottom:0,marginLeft:0,marginRight:0,opacity:0}))])])]},changeDetection:0})}return e})(),Te=(()=>{class e{static \u0275fac=function(s){return new(s||e)};static \u0275mod=A({type:e});static \u0275inj=N({imports:[F,T,T]})}return e})();function eo(e,n){e&1&&(c(0,"div",12)(1,"h1"),d(2,"Dropshipping"),r(),c(3,"p"),d(4,"Entre com suas credenciais"),r()())}function oo(e,n){if(e&1&&u(0,"p-message",3),e&2){let o=l();a("text",o.errorMessage())}}function so(e,n){e&1&&(c(0,"small",8),d(1,"E-mail \xE9 obrigat\xF3rio"),r())}function no(e,n){e&1&&(c(0,"small",8),d(1,"Senha \xE9 obrigat\xF3ria"),r())}var Me=class e{fb=_(ge);auth=_(te);router=_(oe);form=this.fb.nonNullable.group({email:["",[M.required,M.email]],password:["",M.required]});loading=this.auth.loading;errorMessage=this.auth.errorMessage;onSubmit(){this.form.invalid||this.auth.login(this.form.getRawValue()).subscribe({next:()=>this.router.navigate(["/"]),error:()=>{}})}static \u0275fac=function(o){return new(o||e)};static \u0275cmp=x({type:e,selectors:[["app-login"]],decls:16,vars:8,consts:[[1,"login-container"],["styleClass","login-card"],["pTemplate","header"],["severity","error",3,"text"],[1,"login-form",3,"ngSubmit","formGroup"],[1,"field"],["for","email"],["id","email","pInputText","","type","email","formControlName","email","placeholder","seu@email.com","autocomplete","email"],[1,"error"],["for","password"],["id","password","formControlName","password","placeholder","Sua senha","inputStyleClass","w-full",3,"feedback","toggleMask"],["type","submit","label","Entrar","icon","pi pi-sign-in","styleClass","w-full",3,"loading","disabled"],[1,"login-header"]],template:function(o,s){if(o&1&&(c(0,"div",0)(1,"p-card",1),m(2,eo,5,0,"ng-template",2)(3,oo,1,1,"p-message",3),c(4,"form",4),y("ngSubmit",function(){return s.onSubmit()}),c(5,"div",5)(6,"label",6),d(7,"E-mail"),r(),u(8,"input",7),m(9,so,2,0,"small",8),r(),c(10,"div",5)(11,"label",9),d(12,"Senha"),r(),u(13,"p-password",10),m(14,no,2,0,"small",8),r(),u(15,"p-button",11),r()()()),o&2){let t,p;i(3),g(s.errorMessage()?3:-1),i(),a("formGroup",s.form),i(5),g((t=s.form.get("email"))!=null&&t.invalid&&((t=s.form.get("email"))!=null&&t.touched)?9:-1),i(4),a("feedback",!1)("toggleMask",!0),i(),g((p=s.form.get("password"))!=null&&p.invalid&&((p=s.form.get("password"))!=null&&p.touched)?14:-1),i(),a("loading",s.loading())("disabled",s.form.invalid||s.loading())}},dependencies:[$,pe,le,ie,ae,re,ce,me,be,_e,w,Ce,he,ve,xe,$e,ye,Te,F],styles:[".login-container[_ngcontent-%COMP%]{min-height:100vh;display:flex;align-items:center;justify-content:center;padding:1rem;background:#0f172a}.login-card[_ngcontent-%COMP%]{width:100%;max-width:400px}.login-header[_ngcontent-%COMP%]{padding:2rem 2rem 1rem;text-align:center}.login-header[_ngcontent-%COMP%]   h1[_ngcontent-%COMP%]{margin:0;font-size:1.75rem;color:var(--p-text-color)}.login-header[_ngcontent-%COMP%]   p[_ngcontent-%COMP%]{margin:.5rem 0 0;color:var(--p-text-muted-color)}.login-form[_ngcontent-%COMP%]{display:flex;flex-direction:column;gap:1.25rem;padding:0 2rem 2rem}.field[_ngcontent-%COMP%]{display:flex;flex-direction:column;gap:.5rem}.field[_ngcontent-%COMP%]   label[_ngcontent-%COMP%]{font-weight:500;color:var(--p-text-color)}.error[_ngcontent-%COMP%]{color:var(--p-red-500);font-size:.875rem}[_nghost-%COMP%]     .p-password-input{width:100%}"]})};export{Me as LoginComponent};
