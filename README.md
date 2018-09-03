# L2L Framework

This space includes part of the source of the L2L framework and provides support material for the demo paper:

***Coordinating IoT-enabled Autonomous Processes in Cross Enterprise Service Systems 
Authored by Biqi Zhu, Chenglong Hu, Lin Ye, Hong-Linh Truong and  Liang Zhang***

submitted to [ICSOC 2018](http://icsoc.org/).
## Application Scenario
This demostration bases on the supplychain management for Ship Spare Parts (SSP) problem in China shipping company. 
There are four particiants, namely <strong>Vessel</strong>, <strong>Manager</strong>, <strong>Supplier</strong> and <strong>Logistic</strong>. 

In reality, there exist several uncertainties that either the spare parts would arrive too earlier than the vessel
 (leading to higher expense for warehouding), or they would miss the rendezvous port with the vessel 
 (with great hidden danger threatening future voyage).

The docking time depends on the loading/unloading process of other vessels. If there is no vacancy of the port, 
other following vessels can only anchor and wait. In fact, based on data collected from harbor management, 70%-80% 
bulk cargo carries would be delayed on docking time for serveral days or even a few weeks. On the other hand, sometimes 
there are very short time slots for a vessel to dock. 

All these dynamic uncertainties make current approaches, which are almost done by human and offline communication, 
unfitted and costly.
## System Features Outline
Empowered with IoT technologies business entities can perceive context instantly within an enterprise for optimizing 
service-based business processes. Further, we can share to allow enterprise-to-enterprise coordinators to optimize business 
services under dynamic changes. 

In this system, we demonstrate the L2L framework in which <strong>IoT-enablers</strong>, <strong>Event Gateway</strong>, 
<strong>Context Sharing</strong> and <strong>Policies</strong> are introduced to enable  serverless-based <strong>Cross-Enterprise 
Coordinators</strong> in the cloud to deal with dynamic changes among services across enterprises.
We experimented our prototype around a real SSP problem on shipping along the Yangtze river to validate the effectiveness and
efficiency of L2L framework. The main system features are outlined as following.
### 1. How to capture Iot events,and other asynchronous events
<ul>
   <li>
     <strong>Event Gateway</strong> subscribes the IoT and asynchronous events from <strong>Business Entities</strong>
      through <strong>IoT Hub</strong>.
  <ul>
        <li>The vessel sends its GPS coordinates, current speed, ports to be docked, and estimated arrival time to the
         <strong>IoT Hub</strong>. the vessel may send delay events to <strong> Event Gateway</strong>.
        </li>            
        <li>The wagon can sends its GPS coordinates, moved distance and speed to <strong>IoT Hub</strong>. Moreover,
         when encountering traffic jam, result in timeout, the wagon will pubish the traffic jam event to <strong>Event
          Gateway</strong>.
        </li>
   </ul> 
</ul>
<ul>
      <li>
        <strong>Event Gateway</strong> publishes commands to <strong>Business Entities</strong> through <strong>IoT Hub</strong>.
      <ul>
        <li>
          The <strong>Event Gateway </strong>sends message about delivery back to vessel. 
        </li>
        <li>
          The wagon subscribes the new rendezvous port notification from <strong>Event Gateway</strong>.
        </li>
      </ul>
 </ul>     
   
### 2. How these events go through those components
<ul>
  <li> Using <strong>Event Gateway</strong> as dispatcher, events can flow smoothly between <strong>Enterprise Information System</strong> and
   <strong>Businesses Entities</strong> without changing its original framework.
   <ul>
        <li> <strong>Event Gateway</strong> collects the IoT data, feeds them into IoT shadows,and encapsulates them diverse IoT-enable services.</li>
        <li> <strong>Event Gateway</strong> invokes engine services to transforms these events into the workflow envents or  deal with them directly.</li>
        <li>The vessel process invokes these services to acquire the IoT data listens to the status changes of vessel and delay messages. and
         the wagon process might call services to send the rendezouvs port information to <strong>Event Gateway</strong>.</li>
    </ul>
</ul>
<ul>
  <li><strong>Enterprise Coordintor</strong> takes responsibility for informing <strong>Decision Making</strong> of changes. 
  <ul>
  <li> Due to multiple times traffic jam, the wagon cannot reach the destination as expectation time. the wagon process perceives the
   change, and re-plan all pathes to the candidate ports. Finally through the <strong>Enterprise Coordintor</strong>, The traffic
   jam message reaches to the <strong> Logistic Vessel Coordinator</strong>.</li>
</ul>
</ul>
<ul>
   <li>The enterprise can share public data and events to <strong>Global IoT Hub</strong> , and they are can be subscribed by <strong>Relay 
   Station</strong> in <strong>Cross-Enterprise Coordinators</strong> or other enterprises.
   <ul>
   <li>The delay events from <strong>Vessel Businesss Entity</strong> are shared to <strong>Global IoT Hub</strong>.
    Through <strong>Public Channel</strong>, they can be captured by <strong>Relay Station</strong> , then triger the decision-making.</li>
   </ul>
</ul>

### 3. How the coordinator initiates, manage collaborations between two enterprises 
<ul>
 <li>
     Similar configuration can be extracted outside as Enterprise Policies. We can use them in <strong>Event Gateway</strong>,
      <strong>Enterprise Coordintor</strong>, and <strong>Context Sharing</strong>. 
 <ul>
       <li>When events incoming, we can check the message header to find which event type we receive and which policy type they use. Then we can dynamicly orchestrare
        the services specified in confiure policy files to deal with the dynamic changes. We can easily add new policy, and develop and publish the specified services.
       </li>
  </ul>
</ul>
<ul>
     <li>Based on the specificed policy, <strong>Cross-Enterprise Coordinators</strong> can invoke the apropriated coordination functions as services
      to tackle dynamic changes.
 <ul>
       <li>As mentioned earlier, the LVC can capture the delay events from the <strong>Vessel Business Entity</strong>, In some concrete situation, the event
        type is IoT_DELAY and the policy type is  fixed-destination. Then we call functions to extract the message and the invoke the the specified service to
         decide the rendezvous port.  
       </li>
  </ul>
</ul>


## Video
[![placeholder](images/placeholder.png)](https://www.dropbox.com/s/sleajluq3a0xazm/ICSOC2018_demo_l2l_v6.mp4?dl=0)




## User guide
- *`Project Structure`*
```console
L2L
├── BusinessEntities
│   ├── vesselIoT
│   └── wagonIoT
├── Coordinators
│   ├── lvc
│   ├── msc
│   ├── slc
│   └── vmc
├── Enterprises
│   ├── logistics-A
│   ├── manager-A
│   ├── supplier-A
│   └── vessel-A
├── pom.xml
└── README.md
```
-   [`L2L Frontend`](https://github.com/i-qiqi/L2L/tree/lambda)
    > Attention: In order to perform as demo shows, The project must be coordinated with the `L2L Frontend` project.
- if you want to know how to run the system , you can see the [`user guide `](userguide.md) for L2L Backend
