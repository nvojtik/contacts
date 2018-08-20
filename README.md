# contacts

A simple contact book REST API built with Java Spark Framework and ElasticSearch. Contacts are identified by a unique key consisting of their name in lowercase with no spaces. For example, the unique key for a contact named "Jake Evans" is "jakeevans". The following requests are supported:

```http
GET /contact/{name}
```
Passing the unique key as `{name}`, returns a JSON representation of the contact information (or an error message if the record is not found).

```http
GET /contact?pageSize={}&page={}&query={}
```
Returns the `{page}`th page of `{pageSize}` results of a search returned for the Elastic query string `{query}`.

```http
POST /contact
```
Adds a contact based on the JSON representation of the contact information passed in the body of the request. Returns the contact information if added successfully or an error message otherwise.

```http
PUT /contact/{name}
```
Updates the contact information of the contact with the unique key `{name}` based on the JSON representation of the contact information passed in the body of the request. Returns the udpated contact information if updated successfully or an error message otherwise.

```http
DELETE /contact/{name}
```
Deletes the contact information of the contact with the unique key `{name}`. Returns the deleted contact information if deleted successfully or an error message otherwise.
