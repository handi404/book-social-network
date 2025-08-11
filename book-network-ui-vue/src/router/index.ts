import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    component: () => import("../layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        name: "Home",
        component: () => import("../views/BookList.vue"),
      },
      {
        path: "/my-books",
        name: "MyBooks",
        component: () => import("../views/MyBooks.vue"),
      },
      {
        path: "/my-borrowed-books",
        name: "MyBorrowedBooks",
        component: () => import("../views/BorrowedBookList.vue"),
      },
      {
        path: "/my-returned-books",
        name: "MyReturnedBooks",
        component: () => import("../views/ReturnedBooks.vue"),
      },
      {
        path: "/details/:bookId",
        name: "BookDetails",
        component: () => import("../views/BookDetails.vue"),
      },
      {
        path: "/books/manage",
        name: "ManageBook",
        component: () => import("../views/ManageBook.vue"),
      },
      {
        path: "/manage/:bookId",
        name: "ManageBookById",
        component: () => import("../views/ManageBook.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
