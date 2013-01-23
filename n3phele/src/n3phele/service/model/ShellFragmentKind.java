package n3phele.service.model;
/**
*
* (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
* 
* Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
* except in compliance with the License. 
* 
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
*  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
*  specific language governing permissions and limitations under the License.
*
*/

public enum ShellFragmentKind {
    script,
    forCommand,
    variable,
    block,
    createvm,
    option,
    literalArg,
    on,
    log,
    destroy,
    variableAssign,
    expression,
    passThru,
    pieces,
    fileList,
    fileElement,
    //
    // Expression related
    //
    functionExpression,
    conditionalExpression,
    logicalORExpression,
    logicalANDExpression,
    equalityExpression,
    relationalExpression,
    additiveExpression,
    multiplicativeExpression,
    unaryExpression,
    identifier,
    constantLong,
    constantDouble,
    constantString,
    constantBoolean
    
}
