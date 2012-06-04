A Scalable Language
============================

<div style="text-align:center; padding-top: 130px">
    <img src="img/scala_logo_copy.png" alt="scala"/>
</div>

---

about("Konrad Malawski")
========================

![](img/about_me.png)

---

Agenda
======

* Who & Why?
* From Java to Scala
* Implicits

---

Scala - short history
=========================

<img src="img/odersky.jpg" alt="Martin Odersky" height="200" style="float:right;"/>

* Developed at École Polytechnique Fédérale de Lausanne <br/>
  by **Martin Odersky**
    * Odersky is primarily responsible for **Java generics** <br/>
    (and has apologized)
* First version in 2004
* Because Martin has grown tired of legacy constraints and "don't break existing code" blocking serious innovation in Java

---

Scala - "what?"
================

Scala can be described as:

* The child of Java and Lisps / Erlang
* A Object Oriented AND Functional programming language
* One of the world's most sophisticated type systems put to use
* Inherently "ready" for parallel computation - immutable data, actors as abstraction over threads (kudos to *erlang*)
* Scala compiles to plain-old java bytecode, and **runs on the JVM**, as well as on CLR. Though the JVM version is the "primary" impl.

---

Trivia: The logo
=========================

![](img/scala_photo.jpg)

<center>That's why so many books about Scala have Stairs on them ;-)</center>

---

About ScalaSlide
================

**ScalaSlide** is a small tool based on SBT, Scala, Landslide and Markdown I've developed for this presentation.

It's available here: [github.com/ktoso/scalaslide](https://github.com/ktoso/scalaslide), on GPLv3.

ScalaSlide:

* extracts all code from slides
* compiles and **runs tests each of the code blocks**
* if the presentation is valid, generates the landslide presentation (what you see now)

<div style="border-radius: 10px; background: #EEEEEE; padding: 20px; text-align: center; font-size: 1.5em">
  <b>Each block of code in this presentation - compiles and passes tests</b>!
</div>


---

Hello World!
============

    !scala
    object Main extends App {
      println("Hello World!")
    }

Tip: **object** is a built-in singleton pattern - can be used similarily to "static".

---
Type inference
==============

---

Type inference
===================================

The type of a variable, and method can be **inferred**.

    !scala
    val a: Int = 12
    // a = 44 // cannot re-assign to val
    
    var b = 12 // Int
    b = 41
    // b = "" // type missmatch cannot assign String to Int

Different ways to define a method:

    !scala
    def anInt(): Int = { return 42 }
    def anInt2(): Int = 42
    def anInt3() = 42
    def anInt4 = 42
    
And yes, you can define them inline.

---
Type inference - by hand
======================

Let's **infer** the return type of this method (using Scala's **local type inference**).

    !java
    class Animal
    class Dog extends Animal
    class Cat extends Animal
    
    def test(name: String): ???? = {
      val other: ???? = new Cat
    
      (
        if(name == "dog") new Dog: ???? 
        else other: ????
      ): ????
    }

Tip: This gets more interesting when we get to **traits**.

---
Type inference - by hand #2
===========================

Another sligtly more tricky example:

    !java
    // exclude from scalaslide, won't compile
    
    def yay(n: Int): ??? = 
      if(i > 0) yay(i - 1): ???
      else 0: ????


The Type Inferencer *will fail* trying to infer the return type of this method!
We have to define it explicitly when dealing with recursion.

There exist smarter type inference algorithms [Hindley Millner](https://www.google.pl/webhp?sourceid=chrome-instant&ie=UTF-8&ion=1#hl=pl&output=search&sclient=psy-ab&q=hindley%20millner&oq=&aq=&aqi=&aql=&gs_l=&pbx=1&fp=71c1975d38af98d3&ion=1&bav=on.2,or.r_gc.r_pw.r_cp.r_qf.,cf.osb&biw=1280&bih=1262) being a great example. (It's used by **Haskell**)

---

Uniform Access Principle
========================

---

Uniform Access Principle
========================

Also known as: "Don't generate useless setters/getters ahead of time" ;-)

    !scala
    object Language {
      val name = "Scala"
    }
    
    Language.name should equal ("Scala")

You can change it into a method (in fact, it always was a method!), without breaking client code

    !scala
    object Language {
      def name() = "Scala"
    }
    
    // client code remains unchanged
    Language.name should equal ("Scala")

---

Uniform Access Principle with `var`
===================================

The same migration can be done with `var`iables:

    !scala
    class It(var name: String)
    
    val it = new It("rocks")
    it.name = "renamed"
    
    it.name should equal ("renamed")

and the modified version:

    !scala
    class It(private var _name: String) {
      def name = _name
      def name_=(name: String) { _name = name }
    }
    
    // client code remains unchanged!
    val it = new It("rocks")
    it.name = "renamed"
    
    it.name should equal ("renamed")

Which is why we're *fine* with `public val` fields in Scala!

---
Case class
==========

---
Boilerplate killer: case class
===============================

A simple POJO:

    !java
    public class Person {
      private String name;
      private String surname;
      private Integer age;
      
      public Person(String name, String surname, Integer age) {
        this.name = name;
      }
      
      public void setName(String name) { this.name = name; }
      public String getName() { return name; }
      public void setSurname(String surname) { this.surname = surname; }
      public String getSurname() { return surname; }
      public void setAge(Integer age) { this.age = age; }
      public Integer getAge() { return age; }      
      /* 3 times! */
      
      public int hashCode() { /**/ }
      public boolean equals(Object obj) { /**/ }
    }

So much code... Which you generate anyway. And now in Scala...

---

Boilerplate killer: case class
===============================

The same (almost) class in Scala

    !scala
    case class Person(name: String, surname: String, age: Int)

The compiler will generate all we need, including nice `toString`, `##`, `apply`, `unapply` methods.

    !scala
    case class CasePerson(name: String, surname: String, age: Int)
    CasePerson("A", "B", 0).toString should equal ("CasePerson(A,B,0)")
    
    class Person(name: String, surname: String, age: Int)
    new Person("A", "B", 0).toString == "Person@309f8c02"

---
Pattern Matching
================

---
Case Class + Pattern Matching
=============================

Let's take a look at two new features now:

* **default argument values**
* and **pattern matching**

Example:

    !scala
    case class Capybara(name: String = "Not Caplin")
    val any: Any = new Capybara
    
    def isCaplinCapybara(any: Any) = any match {
      case Capybara("Caplin") => true
      case _ => false
    }
    
    isCaplinCapybara(new Capybara("Caplin")) should be (true)
    isCaplinCapybara(new Capybara) should be (false)

The `_` case is quite simply "I don't care what".

Notice that `match` returns a value! His friends `if` and `for` can return values too, by the way.

---
Pattern matching with if statements
===================================

You can be smarter about how you match:

    !scala
    case class Human(name: String)
    case class Capybara(name: String)
    
    val capy: Any = new Capybara("Charlie") // if we don't force Any type, the 
    // compiler won't allow the Human case bellow, because it's impossible to reach!
    
    val hello = capy match {
      case Human(name) if name != "" => "Hello human " + name
      
      case Capybara(name) if name contains "a" => "Hello capybara with an A!"
      
      case human @ Human => "Hello Human without a name!"
      
      case _ => "Who are you?"
    }
    
    hello should equal ("Hello capybara with an A!")
    
Did you notice we don't need `break` anywhere? `match` is not `switch`!

---
Apply/unapply - objects as functions!
================================================

Scala makes heavy use of two "magic" methods, `apply` and `unapply`.
Thanks to them you can write code like this:

    !scala
    object Twice {                              
      def apply(x: Int): Int = x * 2
      def unapply(z: Int): Option[Int] = if (z % 2 == 0) Some(z / 2) else None  
    }

    val in = 42
    val doubled = Twice(42) // same as: Twice.apply(21)
    
    doubled should equal (84)
    
    val unapplied = doubled match { case Twice(n) => n }
     
    unapplied should equal (in)


The object here is called a **Companion Object**. 

It has the same name as some class. This is actually exactly what a case class would generate for us automatically - and why case classes work so well with pattern matching etc!

---
Extractors - a shortcut
=======================

`unapply` can be called even more transparently:

    !scala
    object Twice {
      def unapply(z: Int): Option[Int] = if (z % 2 == 0) Some(z / 2) else None
    }
    
    val Twice(half) = 42
    
    half should equal (42 / 2)

Which is great for deconstructing objects with many fields.

---

Named Parameters
================

    !scala
    case class Human(
      name: String,
      middlename: Option[String] = None,
      lastname: String) {
      
      override val toString = "%s %s%s".format(
        name, 
        middlename.getOrElse(""), 
        lastname) 
    }
    
    val me = new Human(name = "Konrad", lastname = "Malawski")
    
    me.toString should equal ("Konrad Malawski")
    
---

Recursion
===================

---

Recursion
===================

Recursion is a big part of Scala, as with any functional language.

Notice that the type inferencer won't be able to determine the Return Type of such method - it itself would fall into reccurent (infinite) calls! 

    !scala
    def even(n: Long): Boolean = n match {
      case 1 => false
      case 0 => true
      case _ => even(n - 2)
    }
    
    even(4) should be (true)

---

Infinite Reccursion
===================

Scala is able to run **infinitely recursive method calls**!

    !java
    // excluded from scalaslide tests
        
    import annotation.tailrec
    
    @tailrec
    def infinite(n: Int): Unit = infinite(n)
    
    infinite(1111) // will never return, and will never throw stackoverflow!

The above call will **never finish** and it will never throw an **StackOverflowException** like Java would!

`@tailrec` is an **assertion** that the compiler will be able to **Tail-Call-Optimize** this method. 

---
How can Scala do that but JVM not!?
===================================

So... Recursive calls would typically look (and die) like this:

<img src="img/overflow.png"/>

But using **tail-calls** it looks like this:

<img src="img/tail.png"/>

In fact, the Scala compiler is able to *transform such recursive methods to loops*! 

Images from [blog.richdougherty.com (2009)](http://blog.richdougherty.com/2009/04/tail-calls-tailrec-and-trampolines.html).

---
Recursion trick #2: Trampolines
===========

Automatically translating a recursive call to a loop only works if the method calls itself, and no other functions in it's tail.

If you need "real" recursion without growing the stack you'll need to use a technique called Trampolining:

![image](img/trampoline.png)

Trampolines have helpers implemented in: [scala.util.control.TailCalls](http://www.scala-lang.org/api/current/index.html#scala.util.control.TailCalls$).

    !scala
    import scala.util.control.TailCalls._
    
    def isEven(xs: List[Int]): TailRec[Boolean] = // define "what to do next"
      if (xs.isEmpty) done(true) else tailcall(isOdd(xs.tail))

    def isOdd(xs: List[Int]): TailRec[Boolean] =
      if (xs.isEmpty) done(false) else tailcall(isEven(xs.tail))

    val seq: Seq[Int] = 1 to 100000
    isEven(seq.toList).result

Why `toList`? Because `List`s have methods to cut of their `head` and `tail`, much like lisp / prolog / erlang.

---

Lazy evaluation explained
=========================

---

Lazy evaluation 
===============

Can you notice the previous slide containing lazy lazy evaluating code? 

    !scala
    import scala.util.control.TailCalls._
    
    def isEven(xs: List[Int]): TailRec[Boolean] =
      if (xs.isEmpty) done(true) else tailcall(isOdd(xs.tail))

    def isOdd(xs: List[Int]): TailRec[Boolean] =
      if (xs.isEmpty) done(false) else tailcall(isEven(xs.tail))

    isEven((1 to 10).toList).result

Tip: it's in `tailcall`.

    !java
    def tailcall[A](rest: => TailRec[A]): TailRec[A]

Notice the **Type** of the parameter: ` => TailRec[A]`, <br/>
which can be read as `Unit => TailRec[A]` (almost).

Let's learn about function literals and come back to this example.

---
Function literals via `=>`
=================

The `{ => }` syntax allows us to define functions (closures) in a very nice way:

    !scala
    val fun: Function1[Int, Int] = { i => i * 2 }
    
    fun(2) should equal (4)

Notice that the type inferencer inferred `i` being an `Int` value.

---
Treating Methods as Functions
=============================

It's interesting how you can use methods in the same manner,
by creating a **Partially Applied Function** (that's what the trailing `_` does in this example):

    !scala
    object A {
      def foo(i: Int) = i * 2
    }
    
    // create a partially applied function from the foo method
    val theFun: Function1[Int, Int] = A.foo _ 

When you get such Function, you can pass it to other functions/methods, 
it's a "first class citizen" in the language.

---
Functions as arguments
======================

You can pass a function to another function, like this:

    !scala
    def applyIt(function: String => String, arg: String) =
      function(arg)
      
    val shout = { s: String => s + "!" }
    
    applyIt(shout, "Hello") should equal ("Hello!")   

---
Call-By-Name == lazy argument eval
==================================

By adding `=>` before the expected type, we can delay or avoid execution of code.

    !scala
    // call-by-name doIt
    def byNameDoIt[T](it: => T , really_? : Boolean) = really_? match {
      case true => Some(it)
      case _ => None
    }
    
    byNameDoIt(throw new Exception("Fail!"), really_? = false)
    // and it didn't throw the exception!

It's like in **Haskell**, but in Haskell *call-by-name* is the default way of handling arguments - in Scala it's optional.

---
Laziness in `tailcall` explained
================================

Using this knowlage we can now understand why `tailcall(a: RailRec[T]): Rec[T]` can actually work!

    !scala
    import scala.util.control.TailCalls._
        
    // def tailcall[A](rest: => TailRec[A]): TailRec[A]
        
    def isEven(xs: List[Int]): TailRec[Boolean] =
      if (xs.isEmpty) done(true) else tailcall(/*lazy...*/ isOdd(xs.tail))

    def isOdd(xs: List[Int]): TailRec[Boolean] =
      if (xs.isEmpty) done(false) else tailcall(/*lazy...*/ isEven(xs.tail))

Remember the **Trampoline**!

![image](img/trampoline.png)

---
One more `lazy` trick
===================

---
One more `lazy` trick
===================

`lazy val`ues are a nice "freebie" we get from Scala. As the name implies, they're evaluated only when they're needed, and then cached!

    !scala
    object Lazy {
      var major = 1
      var minor = 2
    
      lazy val firstCalledAtVersion = "%d.%d".format(major, minor)
    }
    
    Lazy.firstCalledAtVersion should equal("1.2")
    
    Lazy.major = 111
    
    Lazy.firstCalledAtVersion should not equal ("111.2")
    Lazy.firstCalledAtVersion should equal ("1.2")

It's a nice trick to pull heavy computation off to the point it's first needed.

---
Welcome to functional-land!
===========================

---
Welcome to functional-land!
===========================

Let's take a look of some of the many ways to play with **Immutable Collections** in Scala.

It's important to remember that we deal with immutable data structures, so:

    !scala
    val l = List[Int]()
    
    // l += 3 // can't add to l!

    val ll = 3 :: l // prepends 3 to the list l
    
    // remember pattern matching? Works here too:
    val head :: tail = ll
    
    head should equal (3)
    tail should equal (Nil)
    
    // there are many useful methods defined on List:
    val merged = ll ++ List(4)
    merged should have size (2)

---
Operator overloading? No.
=========================

These `::` and `++` seem like they've been overloaded, like in C++ or Prolog...

Question: *Does Scala have **Operator Overloading**?*<br/>
Answer: **No**, it only has **funny named methods**.

    !scala
    val a = List(1)
    val b = List(2)
    
    a.++(b)
    // is the same as
    a ++ b
    
    // and it's defined in List[T] as:
    // def ++(list: List[T]): List[T]

We're simply using the "*infix call notation*" here. 

The same can be done with "normaly named methods", like all the `should` calls in the code examples!

---
`map`
========

**Map** is a well-known functional concept of "mapping over a collection of values, with a function taking those and returning some other type".

Let's try transform (*map*) a list from strings to numbers: 

    !c
    // List("1", "2", "3") => List(1, 2, 3) // pseudo-code

The type of the function will therefore be:

    !java
    type neededFunType = List[String] => List[Int]
    // the above is valid Scala, by the way!

To achieve this we'll "map over the list with fun":

    !scala
    val list = List("1", "2", "3")
    
    val ints = list map { _.toInt }
    // which is the same same as: list.map(_.toInt)
    
    ints should have length (3)
    ints should (contain (1) and contain (2) and contain (3))

---
`forEach`
=======

`forEach` does exactly the same thing as `map`, but it returns `Unit`.

Thanks to this, **it's faster** - as it won't build a new collection while mapping over the old one!

    !scala
    type forEachType = _ => Unit

---
`foldLeft`
========

What's the average age of those people?

    !scala
    case class P(name: String, age: Int)
    
    // another way to build a List[P]
    val people = List(P("Bob", 23), P("Alice", 24), P("Kermit", 4))
    
    val avgAge = people.foldLeft(0)(_ + _.age) / people.size
    
    avgAge should equal (17)

Fold - think of it as folding a piece of paper.

---
`filter` + chanining transformations
=========================

Find me all people above the age of 21.

    !scala
    case class P(name: String, age: Int)
    
    // another way to build a List[P]
    val people = P("Bob", 23) :: P("Alice", 24) :: P("Kermit", 4) :: Nil

    val above21 = people filter { _.age > 21 } map { _.name } // and more...
    
    above21 should have length (2)

<br/>
Where does the `::` method come from by the way?

It's defined on `Nil`, and **methods starting with `:` are resolved against the RIGHT operand**.

---

Maybe Monad AKA Option[T]
=====================================

Option[T] is one of the simplest **Monads**, it either has a value, or not.
It implements typical functional operations such as **map** for example...

    !scala
    val maybeNumber: Option[Int] = Some(12)
    maybeNumber map { it => println("The number is: " + it) }
    
    val noNumber: Option[Int] = None
    // won't compile without the type forced, scalac is too smart! :-)
    noNumber map { it => println("Hey, this won't execute!") } 

`Option` is the typesafe answer to null. In fact, it's somewhat like the **NullObject** design pattern.

---
Traits
======

---
Diamonds = Multiple Inheritance Fail
====================

<div style="center">
<img src="img/diamond.png" alt="diamond"/>
</div>

The well known "which name() should I call?" problem.

---

Traits
=========================

A **Trait** may be "mixed-into" a class.

It's _similar_ to multiple inheritance.

    !scala
    class Animal { val isAnimal = true }
    trait Furry { val isFurry = true }

    class Cat extends Animal with Furry

The `extends` keyword is always "first", doesn't matter if it's a trait being extended.

    !scala
    trait Furry
    trait Happy
    class Capybara extends Furry with Happy

---

Traits - Type Linearization
=========

![](img/cat.png)

    !scala
    abstract class Animal;              trait HasLegs extends Animal
    trait FourLegged extends HasLegs;  trait Furry extends Animal     
    class Cat extends Furry with FourLegged

---

Traits - Type Linearization
=========

![](img/cat_1.png)

    !scala
    abstract class Animal;              trait HasLegs extends Animal
    trait FourLegged extends HasLegs;  trait Furry extends Animal     
    class Cat extends Furry with FourLegged

---

Traits - Type Linearization
=========

![](img/cat_2.png)

    !scala
    abstract class Animal;              trait HasLegs extends Animal
    trait FourLegged extends HasLegs;  trait Furry extends Animal     
    class Cat extends Furry with FourLegged

---

Traits - Type Linearization
=========

![](img/cat_3.png)

    !scala
    abstract class Animal;              trait HasLegs extends Animal
    trait FourLegged extends HasLegs;  trait Furry extends Animal     
    class Cat extends Furry with FourLegged

---

Traits - Type Linearization
=========

![](img/cat_4.png)

    !scala
    abstract class Animal;              trait HasLegs extends Animal
    trait FourLegged extends HasLegs;  trait Furry extends Animal     
    class Cat extends Furry with FourLegged

---

Traits - Type Linearization
=========

![](img/cat_5.png)

    !scala
    abstract class Animal;              trait HasLegs extends Animal
    trait FourLegged extends HasLegs;  trait Furry extends Animal     
    class Cat extends Furry with FourLegged

---
Traits - Type Linearization
===========================

What about values and `overrides`?

    !scala
    abstract class Animal {
     def name: String
    }
    
    trait HasLegs extends Animal {
      val name = "legs"
    }
   
    trait FourLegged extends HasLegs {
      override val name = "four"
    }
    
    trait Furry extends Animal {
      val name = "furry"
    }
   
    class Cat extends Furry with FourLegged
    (new Cat).name // four

    // class Cats extends FourLegged with Furry // won't compile
    // <console>:11: error: class Cats inherits conflicting members:
    // value name in trait FourLegged$class of type String  and
    // value name in trait Furry$class of type String
    // (Note: this can be resolved by declaring an override in class Cats.)
    //   class Cats extends FourLegged with Furry

---
Traits - super call routing
===========================

    !scala
    trait A { def name = "A" }

    class B extends A { override def name = "B" }

    class C extends B with A {
      val b = name          // "B"
      val a = super[A].name // "A"
    }
    
    val c = new C
    c.a should equal ("A")
    c.b should equal ("B")

---
Implicits
=========

---

Implicit Conversion
=========================

"The **Static** language that feels **Dynamic**"

    !java
    //... 
    
    val object = new Object
    val clazz = object.whatsMyClass

Will this compile?

If we take *Implicit Conversions* into account... <br/>
**You can't be sure if this code compiles!** (unless you know what "..." is)

Implicit Conversions introduce the **Open World Assumption** to the reasoning about well-typedness and validness of source code! *It depends on the context if this will compile.*

---

Implicit Conversion
=========================

"The **Static** language that feels **Dynamic**"

    !scala
    class RichString(s: String) {
      def tell() = println(s)
    }
    implicit def strin2richString(s: String): RichString = new RichString(s)
    
    "Hello Implicit!".tell()

---
Implicit Conversion
===================

Another example of how implicits can be very useful:

    !scala
    class WeirdList(list: List[_])
    object WeirdList {
      implicit def asWeird(list: List[_]) = new WeirdList(list)
    }
    
    import WeirdList._
    
    def weirdStuff(weird: WeirdList) = ()
    
    weirdStuff(List[Int]()) // will get converted automatically

Which proves to be very useful when creating some kind of Domain Specific Languages for example for configuration.

---
Implicit Parameters
===================

---

Implicits in Action - Android
=============================

Example Java API, to display a message (toast):

    !java
    Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show();

Let's try to remove all boilerplate using: 

* **implicit conversions** 
* and **implicit parameters**!

---

Implicits in Action - Android
=============================

"Pimp my library" pattern for Android's Toast:

    !scala
    class Context
    object Toast { 
      def makeText(c: Context, s: String, l: Int) = 
        new { def show() { /**/ } } // a Structural Type!
    }
    
    class Toastable(s: String) { 
      def toast()(implicit ctx: Context) =
        Toast.makeText(ctx, s, 300).show()
    }
    
    implicit def str2toastable(s: String) = new Toastable(s)
    
    implicit val ctx = new Context // different in Android
    
    // usage:
    "hello!".toast

---
Side note: Structural Types
===========================

A structural type is a type defined only by it's **structure** (methods).

    !scala
    val hasNameMethod = new {
      def name = "Konrad"
    }
    
    hasNameMethod.name should equal ("Konrad")
    
Works! But: **Structural Types use reflection underneeth, so AVOID THEM** (because that makes them _really slow_)!

---

Implicit Conversion vs. other langs
===================================

They are better than **Ruby modules**:

* You can change behaviour in a well defined scope - just where the {{implicit}} is reachable, ruby modules work globally

They are better than C# / Java **extension methods** [JEP-126](http://openjdk.java.net/jeps/126):

* Extension methods can't make an object implement an interface, implicits can
* Extension methods can't add fields, implicits can

And...

* **Typesafe!**


---

"Pimp My Library" pattern
=========================

* Use implicit conversions to add behaviour to "someone else's code"
* It's safe because you don't change it in other parts of the system
* More powerfull than "just extension methods", you can add (seemingly) fields and implement interfaces

---
A Scalable Language
===================

---
A Scalable Language
===================

DSLs!
    
    !scala
    import com.foursquare.rogue.Rogue._
    People where (_.age gt 21) findAndModify
      (_.knowScala setTo true) and  
      (_.knowLanguages addToSet 'Scala) updateOne()

---
A Scalable Language
===================

Parallelism!

    !scala
    List(1, 2, 3, 4).par map { _ * 2 }

Actor based parallelism!

    !java
    actor ! DoThings("with Scala!") // hello erlang?

---
A Scalable Language
===================

Scala indeed is a Scalable language...

* It provides enough language tools to implement your **custom control structures** - thanks to **call-by-name** and syntactic sugar here and there
* It supports the **functional programming paradigm**, but does not get in your way of doing things,
* You can easily adjust old code using **implicit conversions**
* Scala has a **Dynamic** type! You can indeed have dynamic code in this static language, if you need it,
* It's **Elegant** and ... *"**Simple, but Hard.**"*,
* Scala has **Macros**! You can modify the compiler output thanks to tree transformations you implement in Scala itself,
* The **scalac** compiler has a modular structure and you can write **compiler plugins**.

---

Thanks
======

<div style="text-align:center; font-size: 1.5em">
  Dziękuję za uwagę!<br/>
  ありがとう！

  
  <br/>
  <br/>
  <br/>
  <img src="img/me.png">

  <br/>
  <br/>
  <a href="http://www.blog.project13.pl">blog.project13.pl</a>
</div>
