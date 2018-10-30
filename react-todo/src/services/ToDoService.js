import axios from "axios";

const baseUrl = "http://localhost:9000/todos";

const getAllTodos = () => {
  const request = axios.get(baseUrl);

  return request.then(response => response.data);
};

const createTodo = newToDo => {
  const request = axios.post(baseUrl, newToDo);

  return request.then(response => response.data);
};

const deleteTodo = id => {
  const request = axios.delete(`${baseUrl}/${id}`);

  return request.then(response => response.data);
};

export default { getAllTodos, createTodo, deleteTodo };
