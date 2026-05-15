package app.persistence;

import app.entities.Status;

public record OrderDetails(String remark, Status status) {
}
