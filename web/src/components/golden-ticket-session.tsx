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
  const { remainingQuantity, status } = useRemainingQuantity(sessionId);
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

      <div
        className={`ticket-card ${remainingQuantity === 0 ? "sold-out" : ""}`}
      >
        <h2 className="ticket-title">Golden Ticket</h2>
      </div>

      {status === "success" && (
        <div className="inventory-status">
          <div
            className={`qty-text ${remainingQuantity! <= 2 && remainingQuantity! > 0 ? "low" : ""}`}
          >
            {remainingQuantity! > 0
              ? `${remainingQuantity} Tickets Left`
              : "SOLD OUT"}
          </div>
        </div>
      )}

      {status === "error" && (
        <div className="inventory-status">
          <div className={`qty-text sold-out`}>
            Error while loading inventory
          </div>
        </div>
      )}

      {status === "pending" && (
        <div className="inventory-status">
          <div className={`qty-text`}>Loading inventory...</div>
        </div>
      )}

      {status === "success" && (
        <div className="purchase-controls">
          <button
            className="buy-button"
            onClick={handleBuy}
            disabled={mutation.isPending || remainingQuantity === 0}
          >
            {mutation.isPending
              ? "Verifying..."
              : remainingQuantity! > 0
                ? "Buy Now"
                : "Out of Stock"}
          </button>

          {errorMsg && <div className="error-toast">{errorMsg}</div>}
          {purchaseCount > 0 && !errorMsg && (
            <span className="success-badge">Owned: {purchaseCount}</span>
          )}
        </div>
      )}
    </div>
  );
}
