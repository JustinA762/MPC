"use strict";(self.webpackChunkcreate_project_docs=self.webpackChunkcreate_project_docs||[]).push([[6937],{3905:(e,t,r)=>{r.d(t,{Zo:()=>d,kt:()=>h});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function l(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var s=n.createContext({}),p=function(e){var t=n.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},d=function(e){var t=p(e.components);return n.createElement(s.Provider,{value:t},e.children)},c="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,d=l(e,["components","mdxType","originalType","parentName"]),c=p(r),u=o,h=c["".concat(s,".").concat(u)]||c[u]||m[u]||a;return r?n.createElement(h,i(i({ref:t},d),{},{components:r})):n.createElement(h,i({ref:t},d))}));function h(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=u;var l={};for(var s in t)hasOwnProperty.call(t,s)&&(l[s]=t[s]);l.originalType=e,l[c]="string"==typeof e?e:o,i[1]=l;for(var p=2;p<a;p++)i[p]=r[p];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}u.displayName="MDXCreateElement"},2273:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>s,contentTitle:()=>i,default:()=>c,frontMatter:()=>a,metadata:()=>l,toc:()=>p});var n=r(7462),o=(r(7294),r(3905));const a={sidebar_position:4},i="Development Environment",l={unversionedId:"development-plan/development-environment",id:"development-plan/development-environment",title:"Development Environment",description:"The required hardware and software to be used to develop the project. This includes the selected IDE, compilers, editors, test tools, etc. Map the effort of hardware and software setting up as tasks as well and mark your chart for the completion of such tasks.",source:"@site/docs/development-plan/development-environment.md",sourceDirName:"development-plan",slug:"/development-plan/development-environment",permalink:"/project-multi-purpose-camera/docs/development-plan/development-environment",draft:!1,editUrl:"https://github.com/Capstone-Projects-2023-Spring/project-multi-purpose-camera/edit/main/documentation/docs/development-plan/development-environment.md",tags:[],version:"current",lastUpdatedBy:"Tyler",sidebarPosition:4,frontMatter:{sidebar_position:4},sidebar:"docsSidebar",previous:{title:"Schedule",permalink:"/project-multi-purpose-camera/docs/development-plan/schedule"},next:{title:"Version Control",permalink:"/project-multi-purpose-camera/docs/development-plan/version-control"}},s={},p=[{value:"Required Hardware",id:"required-hardware",level:2},{value:"Selected IDE",id:"selected-ide",level:2},{value:"Compilers",id:"compilers",level:2},{value:"Test Tools",id:"test-tools",level:2}],d={toc:p};function c(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},d,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"development-environment"},"Development Environment"),(0,o.kt)("p",null,(0,o.kt)("em",{parentName:"p"},"The required hardware and software to be used to develop the project. This includes the selected IDE, compilers, editors, test tools, etc. Map the effort of hardware and software setting up as tasks as well and mark your chart for the completion of such tasks.")),(0,o.kt)("h2",{id:"required-hardware"},"Required Hardware"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"Raspberry Pi"),(0,o.kt)("li",{parentName:"ol"},"Raspberry Pi Camera with Night Vision"),(0,o.kt)("li",{parentName:"ol"},"MicroSD reader & MicroSD card"),(0,o.kt)("li",{parentName:"ol"},"Microphone")),(0,o.kt)("p",null,"Required Services"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"Amazon Web Service Cloud (AWS):\nAmazon Web Services (AWS) has been utilized to establish the cloud environment for this project. The active server will be set up using AWS Elastic Compute Cloud (EC2), which is a virtual instance of Linux on the AWS cloud. The RestAPI server will be hosted online with AWS Lambda, and the server connection will be managed by API Gateway. The Restful API server is linked to AWS Relational Database Service (RDS) to store user and system data and AWS Simple Storage Service (S3) to store file data.")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"Amazon Elastic Compute Cloud (EC2)\n"))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"AWS Lambda\n"))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"Amazon API Gateway\n"))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"Amazon Relational Database Service (RDS)\n"))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"Amazon Simple Storage Service (S3)\n")))),(0,o.kt)("ol",{start:2},(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"Java with Android SDK:\nThe Java programming language is a prerequisite for this project as it is utilized to develop the Android application. This language is primarily employed in conjunction with the Android SDK, which is the Standard Development Kit for Android application development. The SDK provides a comprehensive framework for interacting with Android application development tools.")),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"Python 3.8 or above:\nThe Python programming language is utilized to develop the device code on MPC camera devices that operate on the Raspberry Pi, as well as the server code on AWS EC2 server and AWS Lambda Restful API with API Gateway. The OOP design of this language is highly suitable for this project, which requires collaborative efforts from multiple programmers. The OOP design allows for efficient task division and optimized utilization of workflow and time, thereby meeting the project's requirements.")),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"MySQL 8.0:\nThe Python programming language is utilized for developing the device code on MPC camera devices that operate on the Raspberry Pi, and also for the server code on AWS EC2 server and AWS Lambda Restful API with API Gateway. The language's OOP design is highly suitable for this project, which requires collaborative efforts from multiple programmers. It enables efficient division of tasks and optimized utilization of workflow and time, fulfilling the project's requirements."))),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"MySQL Workbench\n"))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre"},"MySQL Connector for Python\n")))),(0,o.kt)("h2",{id:"selected-ide"},"Selected IDE"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"Visual Studios:")),(0,o.kt)("p",null,"Visual Studio is primarily utilized for server-side development. As the server code is executed on AWS, it's necessary to establish a means of interfacing with the server instance from a local machine. Visual Studio includes a plugin known as Remote SSH, which facilitates connection to the AWS EC2 server via SSH and interaction with the virtual computer hosted on AWS. Additionally, Visual Studio offers a range of plugins that may aid in expediting development processes."),(0,o.kt)("ol",{start:2},(0,o.kt)("li",{parentName:"ol"},"Android Studios:")),(0,o.kt)("p",null,"Android Studio is an integrated development environment (IDE) specifically designed for developing Android applications. It is built on the IntelliJ IDEA IDE and operates on the Android operating system. The IDE offers a comprehensive suite of tools for designing, constructing, and testing Android applications. Android Studio includes a range of useful Android development tools, such as the Android emulator, Android SDK (Standard Development Kit) with multiple versions, and a layout editor."),(0,o.kt)("ol",{start:3},(0,o.kt)("li",{parentName:"ol"},"PyCharm:")),(0,o.kt)("p",null,"PyCharm is an integrated development environment (IDE) for Python application development. It offers a python interpreter execution environment, python code debugger, and a code editor with python snippets. This greatly facilitates the development of server code and device code in Python in an efficient manner. PyCharm also integrates several third-party tools, such as Git and draw.io, which help to focus on code development without worrying about managing code or creating diagrams from classes."),(0,o.kt)("h2",{id:"compilers"},"Compilers"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"Visual Studio"),(0,o.kt)("li",{parentName:"ol"},"Android Studios")),(0,o.kt)("h2",{id:"test-tools"},"Test Tools"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"pytest"),(0,o.kt)("li",{parentName:"ol"},"Robot Framework"),(0,o.kt)("li",{parentName:"ol"},"Cucumber")))}c.isMDXComponent=!0}}]);