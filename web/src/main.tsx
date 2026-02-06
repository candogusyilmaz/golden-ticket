import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "./api/query-client";
import { GoldenTicketPage } from "./components/golden-ticket-page";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <GoldenTicketPage />
    </QueryClientProvider>
  </StrictMode>,
);
