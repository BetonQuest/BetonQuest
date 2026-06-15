---
icon: material/ruler-square-compass
---
# Style Definitions

To ensure consistency across the documentation, we use the following style definitions.

## Reference Lists

There are multiple listings of reference material regarding actions, conditions, or other aspects of the plugin.
To simplify the process of maintaining these lists, we use the following style definition.

!!! abstract "Example"

    ## `Example`
    
    __Context__: @snippet:action-meta:online-offline-independent@  
    __Syntax__: `example <param1> [param2] {param3} +[...]`  
    __Description__: An example action.
    
    This description is used to provide a brief explanation of the action if it is not obvious from short description
    above. It may also be used to provide additional information about the parameters or special behaviors of the action.
    
    | Parameter               | Type                   | Explanation                  |
    |-------------------------|------------------------|------------------------------|
    | param1 <br>[Number]     | Required               | What does this parameter do? |
    | param2 <br>List[Number] | Required               | What does this parameter do? |
    | param3 <br>[String]     | Optional <br>[Null]    | What does this parameter do? |
    | param4 <br>[EnumType]   | Optional <br>[NONE]    | What do these parameters do? |
    | param5 <br>[Boolean]    | Flag <br>[false, true] | What does this parameter do? |
    | +[...] <br>[String]     | Additionals            | What do these parameters do? |

    ```YAML title="Examples"
    actions:
      exapl1: "example 55 param2:5,8,33"
      exapl2: "example 55 param2:5,8,33 param5"
      exapl3: "example 55 param2:5,8,33 param3:Test param4:ONE param5:false"
      exapl4: "example 55 param2:5,8,33 param3:Test param4:ONE param5:false +myAdditionalParam:AnyValue"
    ```
    
    *[EnumType]: NONE, ONE, MULTIPLE (The enum type should simply list all possible values)
    
Make sure the style matches using the following checklists:

??? check "Description"

    - [x] The **headline** of the reference list element should be the precise name of the action, condition, etc.
    - [x] The element should be inserted into the list in alphabetical order, making it easy to find.
    - [x] The [**context**](../../../Documentation/Reference/Definition-Encyclopedia.md#context) should be defined according to the implementation. 
    Make sure to use the correct snippet.
    - [x] The [**syntax**](../../../Documentation/Reference/Definition-Encyclopedia.md#syntax) should be defined according to the implementation. 
    Use `<name>` for required parameters, `[name]` for optional and `{name}` for flag parameters. `+[...]` should be used for additional parameters.
    - [x] The short **description** should be written in a way that is easy to understand.
    - [x] The longer **description** may be used to provide additional information about the parameters or 
    special behaviors of the element, but may be omitted if it is not necessary.

??? check "Parameters"

    - [x] If the element has _at least one_ parameter, there has to be a **table** listing the parameters. 
    If the element has no parameters, the table may be omitted.
    - [x] The parameters should be listed in identical order as they are defined in the [**syntax**](../../../Documentation/Reference/Definition-Encyclopedia.md#syntax) above.
    - [x] Each parameters **name** should be defined in the first column and the expected 
    [**data type**](../../../Documentation/Reference/Definition-Encyclopedia.md#basic-types) after a linebreak inside `[]` brackets. 
    - [x] Each parameters [**type**](../../../Documentation/Reference/Definition-Encyclopedia.md#parameter-type) 
    should be defined in the second column and its defaults after a linebreak inside `[]` brackets.
    - [x] The **explaination** of each parameter should be written in a way that is precise yet easy to understand.

??? check "Examples"

    - [x] **Examples** are a good way to show how the element is used and should be included at the end of the 
    elements scope. 
    - [x] There does not need to be an example for every possible combination of parameters but they should 
    visualize the usage of the element.
    - [x] All **examples** should be shown inside the matching section in the script, e.g. `actions` for actions.
    - [x] The **examples** section may never be empty or omitted
    - [x] The **examples** section should always have a reasonable amount of examples.
    - [x] All **examples** preferably have readable names if possible.

??? check "Additional Information"

    - [x] All important keywords should be linked to the [Definition Encyclopedia](../../../Documentation/Reference/Definition-Encyclopedia.md).
    - [x] Attach all relevant glossary entries to the page if there is something missing.

--8<-- "instruction-datatypes.md"
