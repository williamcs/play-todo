import React, { Component } from "react";
import "./ToDo.css";
import ToDoItem from "./components/ToDoItem";
import Logo from "./assets/logo.svg";
import toDoService from "./services/ToDoService";
import ObjectID from "bson-objectid";

class ToDo extends Component {
  constructor(props) {
    super(props);

    this.state = {
      list: [],
      todo: ""
    };
  }

  componentDidMount() {
    toDoService.getAllTodos().then(response => {
      console.log(response);
      this.setState({ list: response });

      this.state.list.map((item, key) => {
        console.log(item._id.$oid);
      });
    });
  }

  createNewToDoItem = event => {
    event.preventDefault();
    const newToDo = {
      _id: {
        $oid: new ObjectID().toHexString()
      },
      title: this.state.todo,
      completed: false
    };

    console.log("newToDo: ", newToDo);
    console.log("_id: ", new ObjectID().toHexString());

    toDoService.createTodo(newToDo).then(newToDo => {
      console.log("newToDo: ", newToDo);
      this.setState({
        list: this.state.list.concat(newToDo),
        todo: ""
      });
    });

    console.log("createNewToDoItem : ", this.setState.list);
  };

  handleKeyPress = e => {
    if (e.target.value !== "") {
      if (e.key === "Enter") {
        this.createNewToDoItem();
      }
    }
  };

  handleInput = e => {
    this.setState({
      todo: e.target.value
    });
  };

  deleteItem = item => {
    const indexToDelete = item.key;
    console.log("item: ", item);
    console.log("indexToDelete: ", indexToDelete);
    toDoService.deleteTodo(item._id.$oid).then(deletedToDo => {
      this.setState({
        list: this.state.list.filter(
          toDo => toDo._id.$oid !== deletedToDo._id.$oid
        )
      });
    });
  };

  render() {
    return (
      <div className="ToDo">
        <img className="Logo" src={Logo} alt="React logo" />
        <h1 className="ToDo-Header">React To Do</h1>
        <div className="ToDo-Container">
          <div className="ToDo-Content">
            {this.state.list.map((item, key) => {
              return (
                <ToDoItem
                  key={key}
                  item={item.title}
                  deleteItem={this.deleteItem.bind(this, item)}
                />
              );
            })}
          </div>

          <div>
            <input
              type="text"
              value={this.state.todo}
              onChange={this.handleInput}
              onKeyPress={this.handleKeyPress}
            />
            <button className="ToDo-Add" onClick={this.createNewToDoItem}>
              +
            </button>
          </div>
        </div>
      </div>
    );
  }
}

export default ToDo;
