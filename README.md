# J2K
J2K means "Json sample to Kotlin".

## Usage
J2K is simple Kotlin Gradle project. You have 2 options.

1. Clone this repo and run J2K main method
1. Use .jar

J2K have 3 args.
1. path to json file
1. Kotlin class name you want
1. path to out file (optional)

First, make .json file you want to convert.

```.json
{
  "id": "acb12345",
  "users": [
    {"id": 1000, "name": "joe"},
    {"id": 1001, "name": "smith"}
  ],
  "arrowed_type": [1,2,3],
  "transaction_fee": 0.3
}
```

Next, run J2K as below

>  java -jar ./build/libs/J2K-1.0-SNAPSHOT.jar sample.json SampleClass

You see Kotlin data class in stdout.

```.kt
import kotlin.Double
import kotlin.Long
import kotlin.String
import kotlin.collections.List

data class MyClass(
  val id: String,
  val users: List<User>,
  val arrowedType: List<Long>,
  val transactionFee: Double
)

data class User(
  val id: Long,
  val name: String
)
```

## Rule, Constraint
- if json object key is `aa_bb_cc` then class name for object is `AaBbCc`.
- if json array key is `aa_bb_ccs` then class name for element in array is  `AaBbCc`.
- if json key is `aa_bb_ccs` then property name is `aaBbCbc`.
- if json value is null then property type is String.
- json number is converted to Long or Double. 

## Tech
- Kotlin
- [Moshi](https://github.com/square/moshi)
- [KotlinPoet](https://github.com/square/kotlinpoet/)

## License
Apache-2.0