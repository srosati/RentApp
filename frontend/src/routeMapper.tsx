import { Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Article from "./pages/Article";
import Login from "./pages/Login";
import Landing from "./pages/Landing";
import Register from "./pages/Register";
import CreateArticle from "./pages/CreateArticle";

export default function RouteMapper() {
  return (
    <div>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="/marketplace" element={<Home/>} />
        <Route path="/" element={<Landing />} />

        <Route path="/createArticle" element={<CreateArticle />} />
        <Route path={`/articles/:id`} element={<Article />} />
      </Routes>
    </div>
  );
}
