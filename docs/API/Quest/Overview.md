---
icon: material/application-export
status: new
---
@snippet:api-state:unfinished@

# Overview

This page intends to give an elaborate overall introduction to the API and all its basic concepts.

<div class="grid" markdown>

!!! abstract "What this page covers"
    - [How the API is structured](#api-structure)
    - [What basic concepts are used in the API](#api-concepts)

!!! info "What this page does not cover"
    - What an API is
    - How an implementation example might look like
    - [How to obtain the API](../Obtaining-API.md)
    - [How to integrate the API into your project](../Overview.md)
    
</div>

## API Structure

The API is structured to be intuitive to navigate and grants you access to all its features with short stream-like paths 
while retaining the ability to inject parts into your own classes to comply with the [Law of Demeter](https://en.wikipedia.org/wiki/Law_of_Demeter).  
The tree-like capsulation of features ensures a narrowing scope as you move down the path.

<div class="grid" markdown style="grid-template-columns: 2fr 1fr;">
!!! abstract "Chart"
    === "Step 1"
        ```mermaid
        ---
        title: Obtain BetonQuestApi
        ---
        flowchart LR
            Service("BetonQuestApiService")
            API@{ shape: procs, label: "BetonQuestApi"}
            Service target@--> API
            target@{ animate: true }
        ```
    === "Step 2"
        ```mermaid
        ---
        title: Access BetonQuestApi Features
        ---
        flowchart LR   
            API@{ shape: procs, label: "BetonQuestApi"}
            subgraph two [Features]
                direction LR
                profiles("profiles()")
                loggerFactory("loggerFactory()")
                actions("actions()")
                more@{ shape: procs, label: "and more..."}
            end
            API --> profiles
            API --> loggerFactory
            API target@--> actions
            API --> more
            target@{ animate: true }
        ```
    === "Step 3"
        ```mermaid
        ---
        title: Access Feature Details
        ---
        flowchart LR
            actions("actions()") 
            subgraph three [Feature Details]
                    direction LR
                    manager("manager()")
                    registry("registry()")
            end
            actions target@--> manager
            actions --> registry
            target@{ animate: true }
        ```
    === "Full"
        ```mermaid
        ---
        title: Overview
        ---
        flowchart LR
            Service("BetonQuestApiService")
            subgraph one [API]
                direction LR
                API@{ shape: procs, label: "BetonQuestApi"}
            subgraph two [Features]
                direction LR
                profiles("profiles()")
                loggerFactory("loggerFactory()")
                actions("actions()")
                more@{ shape: procs, label: "and more..."}
                
                subgraph three [Feature Details]
                        direction LR
                        manager("manager()")
                        registry("registry()")
                end
                actions target3@--> manager
                actions --> registry
                
            end
            API --> profiles
            API --> loggerFactory
            API target2@--> actions
            API --> more
            end
            Service target1@--> API
            target1@{ animate: true }
            target2@{ animate: true }
            target3@{ animate: true }
        ```


!!! info "Explanation"
    === "Step 1"
        On the previous page, we learned [how to obtain the API](../Obtaining-API.md).  
        <br>The highlighted step is equivalent to ``betonQuestApiService.api(yourPlugin)``
    === "Step 2"
        Once the API is obtained, we can access the features of the API.
        Among these features are the `profiles()`, `loggerFactory()`, `actions()` and many more.  
        <br>They are partially discussed in more detail in the [concepts section](#api-concepts) below.  
        Some features have their own pages, which are linked in the sidebar by their name.  
        <br>The highlighted step is equivalent to ``betonQuestApi.actions()``
    === "Step 3"
        Some features have their own sub-features that essentially split up the feature into smaller parts.
        Most factory-type features have a `manager()` and a `registry()` sub-feature.
        Learn more about how BetonQuest uses factories in the [concepts section](#api-concepts) below.  
        <br>The highlighted step is equivalent to ``actions.manager()``
    === "Full"
        The API is structured in a way that it is easy to navigate and therefore allowing you to access all features 
        with short paths while retaining the ability to inject parts into your own classes to comply with the Law of 
        Demeter.  
        <br>The highlighted steps are equivalent to ``betonQuestApiService.api(yourPlugin).actions().manager()``

</div>

## API Concepts

---

<div class="grid" markdown style="text-align: center;">
<div markdown style="text-align: left;">
[:octicons-arrow-left-16: Obtain the API](../Obtaining-API.md){ .md-button .md-button--primary }
</div>
<div markdown style="text-align: right;">
[Learn about Instructions :octicons-arrow-right-16:](../Instruction.md){ .md-button .md-button--primary }
</div>
</div>
