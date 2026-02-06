import { useState } from "react";
import { $api } from "../api/open-api";
import { useRemainingQuantity } from "../hooks/use-remaining-quantity";
import { useQueryClient } from "@tanstack/react-query";

export function GoldenTicketSession({
  sessionId,
  onClose,
}: {
  sessionId: string;
  onClose: (id: string) => void;
}) {
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const queryClient = useQueryClient();
  const quantity = useRemainingQuantity(sessionId);
  const [purchaseCount, setPurchaseCount] = useState(0);

  const mutation = $api.useMutation("post", "/api/buy", {
    onMutate: async () => {
      setErrorMsg(null);
      setPurchaseCount((old) => old + 1);
    },
    onError: () => {
      setPurchaseCount((prev) => prev - 1);
      setErrorMsg("Failed to purchase. Please try again.");
      setTimeout(() => setErrorMsg(null), 3000);
      queryClient.invalidateQueries({
        queryKey: ["get", "/api/inventory", sessionId],
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["get", "/api/inventory", sessionId],
      });
    },
  });

  function handleBuy() {
    mutation.mutate({ params: { query: { sessionId } } });
  }

  return (
    <div className="ticket-session-container">
      <div className="session-header">
        <span className="session-id-badge">{sessionId}</span>
        <button
          className="close-session-btn"
          onClick={() => onClose(sessionId)}
        >
          ✕
        </button>
      </div>

      <div className={`ticket-card ${quantity === 0 ? "sold-out" : ""}`}>
        <h2 className="ticket-title">Golden Ticket</h2>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            fontSize: "0.6rem",
            fontWeight: "bold",
          }}
        >
          <span>ADMIT ONE</span>
          <span>VALUED AT $500</span>
        </div>
      </div>

      <div className="inventory-status">
        <div
          className={`qty-text ${quantity <= 2 && quantity > 0 ? "low" : ""}`}
        >
          {quantity > 0 ? `${quantity} Tickets Left` : "SOLD OUT"}
        </div>
      </div>

      <div className="purchase-controls">
        <button
          className="buy-button"
          onClick={handleBuy}
          disabled={mutation.isPending || quantity === 0}
        >
          {mutation.isPending
            ? "Verifying..."
            : quantity > 0
              ? "Buy Now"
              : "Out of Stock"}
        </button>

        {errorMsg && <div className="error-toast">{errorMsg}</div>}
        {purchaseCount > 0 && !errorMsg && (
          <span className="success-badge">Owned: {purchaseCount}</span>
        )}
      </div>
    </div>
  );
}
