import { Route, Routes } from "react-router-dom";
import Marketplace from "./pages/Marketplace";
import Article from "./pages/Article";
import Login from "./pages/Login";
import Landing from "./pages/Landing";
import Register from "./pages/Register";
import CreateArticle from "./pages/CreateArticle";
import Requests from "./pages/Request";

export default function RouteMapper() {
  return (
    <div>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/proposals" element={<Requests />} />

        <Route path="/marketplace" element={<Marketplace />} />
        <Route path="/" element={<Landing />} />

        <Route path="/createArticle" element={<CreateArticle />} />
        <Route path={`/articles/:id`} element={<Article />} />
      </Routes>
    </div>
  );
}
