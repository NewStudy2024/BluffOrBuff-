# Welcome to the Learning Outcomes Evaluation

Dear students,

Welcome to this Learning Outcomes Evaluation session. The goal is to assess your understanding and mastery of the learning outcomes for this semester as evidenced by your work that was submitted on your personal git account. Remember to answer each question thoroughly by referencing **Java** code and provide clear explanations where necessary.

Best regards,
Kay Berkling

## Ethics Section regarding generative and other forms of AI

The student acknowledges and agrees that the use of AI is strictly prohibited during this evaluation. By submitting this report, the student affirms that they have completed the form independently and without the assistance of any AI technologies. This agreement serves to ensure the integrity and authenticity of the students work, as well as their understanding of the learning outcomes.


## Checklist before handing in your work

* [ ] Review the assignment requirements to ensure you have completed all the necessary tasks.
* [ ] Double-check your links and make sure that links lead to where you intended. Each answer should have links to work done by you in your own git repository. (Exception is Speed Coding)
* [ ] Make sure you have at least 10 references to your project code (This is important evidence to prove that your project is substantial enough to support the learning outcome of object oriented design and coding within a larger piece of code.)
* [ ] Include comments to explain your referenced code and why it supports the learning outcome.
* [ ] Commit and push this markup file to your personal git repository and hand in the link and a soft-copy via email at the end of the designated time period.

Remember, this checklist is not exhaustive, but it should help you ensure that your work is complete, well-structured, and meets the required standards.

Good luck with your evaluation!

# Project Description (70%)


## Link


https://github.com/NewStudy2024/BluffOrBuff-

## TECH STACK

Java, junit for testing units

## What did you achieve? 

My game now works fully it has montecarlo simulation which for i also use multithreading, but i couldnt implement the player analytics :/, outside of that however everything is there.





## Learning Outcomes

| Exam Question | Total Achievable Points | Points Reached During Grading |
|---------------|------------------------|-------------------------------|
| Q1. Algorithms    |           4            |                               |
| Q2.Data types    |           4            |                               |
| Q3.Complex Data Structures |  4            |                               |
| Q4.Concepts of OOP |          6            |                               |
| Q5.OO Design     |           6            |                               |
| Q6.Testing       |           3            |                               |
| Q7.Operator/Method Overloading | 6 |                               |
| Q8.Templates/Generics |       4            |                               |
| Q9.Class libraries |          4            |                               |


## Evaluation Questions

Please answer the following questions to the best of your ability to show your understanding of the learning outcomes. Please provide examples from your project code to support your answers.


## Evaluation Material


### Q1. Algorithms

Algorithms are manyfold and Java can be used to program these. Examples are sorting or search strategies but also mathematical calculations. Please refer to **two** areas in either your regular coding practice (for example from Semester 1) or within your project, where you have coded an algorithm. Do not make reference to code written for other classes, like theoretical informatics.


https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/simulation/MonteCarloSimulator.java

This algorithm is a montecarlo simulation algorithm it simulates thousands of possible poker game outcomes randomly dealing cards that are not dealt yet, then it calculates the propability of winning based on the current cards that we knoe. it also uses multithtreadding to make the simulation faster.

https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/HandEvaluator.java

line 100 isStraigh()  is an algorithm that checks the players hand whether they have a straight, it handles special cases where Ace is low an slides through the hand to find 5 consecutive ranks.


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           4            |                               |


### Q2. Data types

Please explain the concept of data types and provide examples of different data types in Java.
typical data types in java are int, double, float, char, boolean, long, short, byte, String, and arrays. Please provide one example for each of the **four** following data types in your code.

* arrays : arrays are list with fix amount of values athat contains only one type of values,            https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/Card.java  line 22 rank returns an array
* Strings : is a datatype thats not primitive its an obect that has its own methods                      https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/menu/GameMenu.java line 49 
* boolean is a primitive data type its a true or false                                                   https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/Player.java  line 38 
* int: is a primitive data type that can hold 30 bits whole number no decimals.                          https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/Player.java  line 21


*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           4             |                               |



### Q3. Complex Data Structures

Examples of complex data structures in java are ArrayList, HashMap, HashSet, LinkedList, and TreeMap. Please provide an example of how you have used **two** of these complex data structures in your code and explain why you have chosen these data structures.

src/main/java/BluffOrBluff/model/Deck.java line 10 i use ArrayList for dynamic card storage 
    https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/HandEvaluator.java    line 83 it makes grouping the cards easoer by suit  to find flush


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           4             |                               |


### Q4. Concepts of OOP
Concepts of OOP are the basic building blocks of object-oriented programming, such as classes, objects, methods, and attributes. 
Explain HOW and WHY your **project** demonstrates the use of OOP by using all of the following concepts:
* Classes/Objects.  I use classes to represent the main entities of the poker game, such as Card, Player, Deck, and HandEvaluator https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/HandEvaluator.java 
* Methods. I use methods to define the behaviour of classes. They also allow us to perform specific task or actions related to the class for example isFlush on handevaluator     https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/model/HandEvaluator.java    line 83 it makes grouping the cards easoer by suit  to find flush

* Attributes I use attribute to to store properties of object, for example in  Card class. 
Link to the code in your project that demonstrates what you have explained above.

*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|             6           |                               |

### Q5. OO Design
Please showcase **two** areas where you have used object orientation and explain the advantage that object oriented code brings to the application or the problem that your code is addressing.
Examples in java of good oo design are encapsulation, inheritance, polymorphism, and abstraction. 


*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|              6          |                               |



### Q6. Testing
Java code is tested by using JUnit. Please explain how you have used JUnit in your project and provide a link to the code where you have used JUnit. Links do not have to refer to your project and can refer to your practice code. If you tested without JUnit, please explain how you tested your code.
Be detailed about what you are testing and how you argue for your test cases. 

I was testing different edge cases for different hands and me to make sure that the hand evaluator works properly. due to lack of time I started using dictation so it might be that I am misspelling some stuff please excuse me in advance and the lack of time I also couldn't finish up.

Feel free to refer to the vibe coding session where you explored testing. (pair programming - you may link to your partner git and name him/her.)

src/test/java/BluffOrBluff/test/HandEvaluatorUnitTest.java

Test cases usually cover the following areas:
* boundary cases
* normal cases
* error cases / catching exceptions 

*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|         3               |                               |

### Q7. Operator/Method Overloading
An example of operator overloading is the "+" operator that can be used to add two numbers or concatenate two strings. An example of method overloading is having two methods with the same name but different parameters. Please provide an example of how you have used operator or method overloading in your code and explain why you have chosen this method of coding.
The link does not have to be to your project and can be to your practice code.

*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|          6              |                               |



### Q8. Templates/Generics
Generics in java are used to create classes, interfaces, and methods that operate on objects of specified types. Please provide an example of how you have used generics in your code and explain why you have chosen to use generics. The link does not have to be to your project and can be to your practice code.


*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           6             |                               |

### Q9. Class Libraries
Examples of class libraries in java are the Java Standard Library, JavaFX, Apache Commons, JUnit, Log4j, Jackson, Guava, Joda-Time, Hibernate, Spring, Maven, and many more. Please provide an example of how you have used a class library in your **project** code and explain why you have chosen to use this class library. 

Junit src/test/java/BluffOrBluff/test
*your text*

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|            6            |                               |


# Creativity (10%)
Which one did you choose: 

* [ ] Web Interface with Design
* [ ] Database Connected
* [x] Multithreading 
* [ ] File I/O
* [ ] API
* [ ] Deployment



https://github.com/NewStudy2024/BluffOrBuff-/blob/tempsolution/src/main/java/BluffOrBluff/simulation/MonteCarloSimulator.java


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|            10          |                               |



# Speed Coding (20%)
Please enter **three** Links to your speed coding session GITs and name your partner. 


*your text*


Paste your class diagram for your project that you developed during the peer review class here: 

*your image*

It can be done very simply by just copying any image and pasting it while editing Readme.md.


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|            16            |                               |





